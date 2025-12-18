package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Exception.BusinessViolationException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.PurchaseOrderMapper;
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
@DisplayName("Purchase Order Service Tests")
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockLotRepository stockLotRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrder order;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setReference("PROD-001");

        order = new PurchaseOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order.setSupplier(supplier);
        order.setOrderLines(new ArrayList<>());
    }

    @Test
    @DisplayName("Create purchase order with valid data succeeds")
    void createPurchaseOrder_WithValidData_CreatesOrder() {
        CreatePurchaseOrderDTO.OrderLineDTO lineDTO = new CreatePurchaseOrderDTO.OrderLineDTO();
        lineDTO.setProductId(1L);
        lineDTO.setQuantity(10.0);
        lineDTO.setUnitPrice(100.0);

        CreatePurchaseOrderDTO createDTO = new CreatePurchaseOrderDTO();
        createDTO.setSupplierId(1L);
        createDTO.setOrderLines(List.of(lineDTO));

        ReadPurchaseOrderDTO readDTO = new ReadPurchaseOrderDTO();
        readDTO.setId(1L);

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any())).thenReturn(order);
        when(purchaseOrderMapper.toDto(any())).thenReturn(readDTO);

        ReadPurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(createDTO);

        assertNotNull(result);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    @DisplayName("Validate order when pending succeeds")
    void validateOrder_WhenPending_ValidatesSuccessfully() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        purchaseOrderService.validateOrder(1L);

        assertEquals(OrderStatus.VALIDATED, order.getStatus());
        verify(purchaseOrderRepository).save(order);
    }

    @Test
    @DisplayName("Validate order when not pending throws exception")
    void validateOrder_WhenNotPending_ThrowsException() {
        order.setStatus(OrderStatus.VALIDATED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessViolationException.class,
                () -> purchaseOrderService.validateOrder(1L));
    }

    @Test
    @DisplayName("Cancel order when pending succeeds")
    void cancelOrder_WhenPending_CancelsSuccessfully() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        purchaseOrderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Cancel order when delivered throws exception")
    void cancelOrder_WhenDelivered_ThrowsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessViolationException.class,
                () -> purchaseOrderService.cancelOrder(1L));
    }

    @Test
    @DisplayName("Delete order when delivered throws exception")
    void deleteOrder_WhenDelivered_ThrowsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessViolationException.class,
                () -> purchaseOrderService.deletePurchaseOrder(1L));
    }

    @Test
    @DisplayName("Receive order creates stock lots and movements")
    void receiveOrder_CreatesStockLotsAndMovements() {
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setId(1L);
        line.setQuantity(10.0);
        line.setUnitPrice(100.0);
        line.setProduct(product);
        line.setPurchaseOrder(order);
        order.setOrderLines(List.of(line));
        order.setStatus(OrderStatus.VALIDATED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(stockLotRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        purchaseOrderService.receiveOrder(1L);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(stockLotRepository).save(any(StockLot.class));
        verify(stockMovementRepository).save(any(StockMovement.class));
    }
}
