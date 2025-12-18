package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.goodsissue.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.goodsissue.CreateGoodsIssueLineDTO;
import com.example.tricolv2sb.DTO.goodsissue.ReadGoodsIssueDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Exception.BusinessViolationException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.GoodsIssueMapper;
import com.example.tricolv2sb.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Goods Issue Service Tests")
class GoodsIssueServiceTest {

    @Mock
    private GoodsIssueRepository goodsIssueRepository;
    @Mock
    private GoodsIssueLineRepository goodsIssueLineRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockLotRepository stockLotRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private GoodsIssueMapper goodsIssueMapper;

    @InjectMocks
    private GoodsIssueService goodsIssueService;

    private GoodsIssue goodsIssue;
    private Product product;
    private StockLot stockLot;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setReference("PROD-001");
        product.setReorderPoint(5.0);

        goodsIssue = new GoodsIssue();
        goodsIssue.setId(1L);
        goodsIssue.setIssueNumber("GI-001");
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);
        goodsIssue.setIssueLines(new ArrayList<>());

        stockLot = new StockLot();
        stockLot.setId(1L);
        stockLot.setRemainingQuantity(100.0);
        stockLot.setProduct(product);
        stockLot.setEntryDate(LocalDate.now());
    }

    @Test
    @DisplayName("Fetch all goods issues returns list")
    void fetchAllGoodsIssues_ReturnsListOfIssues() {
        ReadGoodsIssueDTO dto = new ReadGoodsIssueDTO();
        when(goodsIssueRepository.findAll()).thenReturn(List.of(goodsIssue));
        when(goodsIssueMapper.toDto(goodsIssue)).thenReturn(dto);

        List<ReadGoodsIssueDTO> result = goodsIssueService.fetchAllGoodsIssues();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Validate goods issue when draft succeeds with FIFO stock consumption")
    void validateGoodsIssue_WhenDraft_ValidatesSuccessfully() {
        GoodsIssueLine line = new GoodsIssueLine();
        line.setId(1L);
        line.setProduct(product);
        line.setQuantity(10.0);
        line.setGoodsIssue(goodsIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(line));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(List.of(stockLot));

        goodsIssueService.validateGoodsIssue(1L);

        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssue.getStatus());
        verify(stockMovementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("Validate goods issue when not draft throws exception")
    void validateGoodsIssue_WhenNotDraft_ThrowsException() {
        goodsIssue.setStatus(GoodsIssueStatus.VALIDATED);
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.validateGoodsIssue(1L));
    }

    @Test
    @DisplayName("Validate goods issue with insufficient stock throws exception")
    void validateGoodsIssue_WhenInsufficientStock_ThrowsException() {
        GoodsIssueLine line = new GoodsIssueLine();
        line.setProduct(product);
        line.setQuantity(200.0);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(line));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(50.0);

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.validateGoodsIssue(1L));
    }

    @Test
    @DisplayName("Validate goods issue when below reorder point throws exception")
    void validateGoodsIssue_WhenBelowReorderPoint_ThrowsException() {
        GoodsIssueLine line = new GoodsIssueLine();
        line.setProduct(product);
        line.setQuantity(96.0);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(line));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.validateGoodsIssue(1L));
    }

    @Test
    @DisplayName("Validate goods issue with no lines throws exception")
    void validateGoodsIssue_WithNoLines_ThrowsException() {
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of());

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.validateGoodsIssue(1L));
    }

    @Test
    @DisplayName("Cancel goods issue when not cancelled succeeds")
    void cancelGoodsIssue_WhenNotCancelled_CancelsSuccessfully() {
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        goodsIssueService.cancelGoodsIssue(1L);

        assertEquals(GoodsIssueStatus.CANCELLED, goodsIssue.getStatus());
    }

    @Test
    @DisplayName("Cancel goods issue when already cancelled throws exception")
    void cancelGoodsIssue_WhenAlreadyCancelled_ThrowsException() {
        goodsIssue.setStatus(GoodsIssueStatus.CANCELLED);
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.cancelGoodsIssue(1L));
    }

    @Test
    @DisplayName("Delete goods issue when draft succeeds")
    void deleteGoodsIssue_WhenDraft_DeletesSuccessfully() {
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        goodsIssueService.deleteGoodsIssue(1L);

        verify(goodsIssueRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete goods issue when not draft throws exception")
    void deleteGoodsIssue_WhenNotDraft_ThrowsException() {
        goodsIssue.setStatus(GoodsIssueStatus.VALIDATED);
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        assertThrows(BusinessViolationException.class,
                () -> goodsIssueService.deleteGoodsIssue(1L));
    }
}
