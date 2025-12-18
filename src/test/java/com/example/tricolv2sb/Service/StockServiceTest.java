package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.product.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.stock.StockSummaryDTO;
import com.example.tricolv2sb.DTO.stock.StockValuationDTO;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Entity.StockLot;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.StockLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock Service Tests")
class StockServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockLotRepository stockLotRepository;

    @InjectMocks
    private StockService stockService;

    private Product product;
    private StockLot stockLot1;
    private StockLot stockLot2;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setReference("PROD-001");
        product.setName("Test Product");
        product.setReorderPoint(10.0);

        stockLot1 = new StockLot();
        stockLot1.setId(1L);
        stockLot1.setRemainingQuantity(50.0);
        stockLot1.setInitialQuantity(100.0);
        stockLot1.setPurchasePrice(10.0);
        stockLot1.setEntryDate(LocalDate.now().minusDays(10));
        stockLot1.setProduct(product);

        stockLot2 = new StockLot();
        stockLot2.setId(2L);
        stockLot2.setRemainingQuantity(30.0);
        stockLot2.setInitialQuantity(50.0);
        stockLot2.setPurchasePrice(12.0);
        stockLot2.setEntryDate(LocalDate.now().minusDays(5));
        stockLot2.setProduct(product);
    }

    @Test
    @DisplayName("Get global stock returns summary for all products")
    void getGlobalStock_ReturnsStockSummaryForAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(80.0);

        List<StockSummaryDTO> result = stockService.getGlobalStock();

        assertEquals(1, result.size());
        assertEquals(80.0, result.get(0).getTotalStock());
        assertFalse(result.get(0).getBelowThreshold());
    }

    @Test
    @DisplayName("Get global stock identifies low stock products")
    void getGlobalStock_IdentifiesLowStockProducts() {
        product.setReorderPoint(100.0);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(50.0);

        List<StockSummaryDTO> result = stockService.getGlobalStock();

        assertTrue(result.get(0).getBelowThreshold());
    }

    @Test
    @DisplayName("Get product stock detail returns FIFO valuation and lots")
    void getProductStockDetail_ReturnsDetailedStockInfo() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(List.of(stockLot1, stockLot2));

        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        assertEquals(80.0, result.getTotalStock());
        assertEquals(860.0, result.getFifoValuation());
        assertEquals(2, result.getLots().size());
    }

    @Test
    @DisplayName("Get product stock detail when not found throws exception")
    void getProductStockDetail_WhenProductNotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stockService.getProductStockDetail(1L));
    }

    @Test
    @DisplayName("Get total valuation calculates correct FIFO value")
    void getTotalValuation_CalculatesCorrectValue() {
        when(stockLotRepository.findAll()).thenReturn(List.of(stockLot1, stockLot2));

        StockValuationDTO result = stockService.getTotalValuation();

        assertEquals(860.0, result.getTotalValue());
        assertEquals(1, result.getTotalProducts());
        assertEquals(2, result.getTotalLots());
    }

    @Test
    @DisplayName("Get total valuation excludes empty lots")
    void getTotalValuation_ExcludesEmptyLots() {
        stockLot2.setRemainingQuantity(0.0);
        when(stockLotRepository.findAll()).thenReturn(List.of(stockLot1, stockLot2));

        StockValuationDTO result = stockService.getTotalValuation();

        assertEquals(500.0, result.getTotalValue());
        assertEquals(1, result.getTotalLots());
    }
}
