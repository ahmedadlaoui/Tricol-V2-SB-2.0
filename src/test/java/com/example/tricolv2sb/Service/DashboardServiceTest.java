package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.dashboard.DashboardDTO;
import com.example.tricolv2sb.DTO.dashboard.StockAlertDTO;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Dashboard Service Tests")
class DashboardServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private GoodsIssueRepository goodsIssueRepository;
    @Mock
    private StockLotRepository stockLotRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Product product;
    private Supplier supplier;
    private PurchaseOrder order;
    private GoodsIssue goodsIssue;
    private StockLot stockLot;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setReference("PROD-001");
        product.setName("Test Product");
        product.setReorderPoint(10.0);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setEmail("supplier@test.com");

        order = new PurchaseOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order.setSupplier(supplier);

        goodsIssue = new GoodsIssue();
        goodsIssue.setId(1L);
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);
        goodsIssue.setIssueNumber("GI-001");
        goodsIssue.setIssueDate(LocalDate.now());
        goodsIssue.setDestination("Workshop");

        stockLot = new StockLot();
        stockLot.setId(1L);
        stockLot.setRemainingQuantity(50.0);
        stockLot.setPurchasePrice(10.0);
        stockLot.setProduct(product);
    }

    @Test
    @DisplayName("Get dashboard stats returns complete statistics")
    void getDashboardStats_ReturnsCompleteStats() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.findAll()).thenReturn(List.of(stockLot));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(50.0);
        when(purchaseOrderRepository.findAll()).thenReturn(List.of(order));
        when(goodsIssueRepository.findAll()).thenReturn(List.of(goodsIssue));
        when(supplierRepository.count()).thenReturn(1L);

        DashboardDTO result = dashboardService.getDashboardStats();

        assertEquals(1, result.getTotalProducts());
        assertEquals(1, result.getTotalStockLots());
        assertEquals(500.0, result.getTotalStockValue());
        assertEquals(1, result.getTotalPurchaseOrders());
        assertEquals(1, result.getPendingOrders());
        assertEquals(1, result.getTotalGoodsIssues());
        assertEquals(1, result.getDraftIssues());
        assertEquals(1, result.getTotalSuppliers());
    }

    @Test
    @DisplayName("Get stock alerts returns CRITICAL for zero stock")
    void getStockAlerts_ReturnsCriticalAlertForZeroStock() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(0.0);

        List<StockAlertDTO> alerts = dashboardService.getStockAlerts();

        assertEquals(1, alerts.size());
        assertEquals("CRITICAL", alerts.get(0).getAlertLevel());
    }

    @Test
    @DisplayName("Get stock alerts returns WARNING for low stock")
    void getStockAlerts_ReturnsWarningForLowStock() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(5.0);

        List<StockAlertDTO> alerts = dashboardService.getStockAlerts();

        assertEquals(1, alerts.size());
        assertEquals("WARNING", alerts.get(0).getAlertLevel());
    }

    @Test
    @DisplayName("Get stock alerts returns empty for healthy stock")
    void getStockAlerts_ReturnsEmptyForHealthyStock() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(50.0);

        List<StockAlertDTO> alerts = dashboardService.getStockAlerts();

        assertTrue(alerts.isEmpty());
    }
}
