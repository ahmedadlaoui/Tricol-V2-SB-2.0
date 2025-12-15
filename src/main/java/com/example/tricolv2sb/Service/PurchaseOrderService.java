package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Exception.PurchaseOrderNotFoundException;
import com.example.tricolv2sb.Repository.StockMovementRepository;
import com.example.tricolv2sb.Repository.StockLotRepository;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Mapper.PurchaseOrderMapper;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Repository.PurchaseOrderRepository;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.SupplierRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService implements PurchaseOrderInterface {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final StockLotRepository stockLotRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ReadPurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAllWithOrderLines()
                .stream()
                .map(purchaseOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReadPurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByIdWithOrderLines(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    public ReadPurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        Supplier supplier = supplierRepository.findById(createPurchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException(
                        "Supplier not found with id: " + createPurchaseOrderDTO.getSupplierId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setStatus(OrderStatus.PENDING);
        purchaseOrder.setSupplier(supplier);

        List<PurchaseOrderLine> orderLines = new ArrayList<>();
        for (CreatePurchaseOrderDTO.OrderLineDTO lineDTO : createPurchaseOrderDTO.getOrderLines()) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + lineDTO.getProductId()));

            PurchaseOrderLine line = new PurchaseOrderLine();
            line.setQuantity(lineDTO.getQuantity());
            line.setUnitPrice(lineDTO.getUnitPrice());
            line.setProduct(product);
            line.setPurchaseOrder(purchaseOrder);
            orderLines.add(line);
        }

        purchaseOrder.setOrderLines(orderLines);
        purchaseOrder.calculateTotalAmount();

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(savedPurchaseOrder);
    }

    public ReadPurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderDTO updatePurchaseOrderDTO) {
        PurchaseOrder existingPurchaseOrder = purchaseOrderRepository.findByIdWithOrderLines(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));

        existingPurchaseOrder.setStatus(updatePurchaseOrderDTO.getStatus());
        if (updatePurchaseOrderDTO.getTotalAmount() != null) {
            existingPurchaseOrder.setTotalAmount(updatePurchaseOrderDTO.getTotalAmount());
        }

        if (updatePurchaseOrderDTO.getOrderLines() != null && !updatePurchaseOrderDTO.getOrderLines().isEmpty()) {
            existingPurchaseOrder.getOrderLines().clear();

            for (UpdatePurchaseOrderDTO.OrderLineDTO lineDTO : updatePurchaseOrderDTO.getOrderLines()) {
                PurchaseOrderLine line;
                if (lineDTO.getId() != null) {
                    line = existingPurchaseOrder.getOrderLines().stream()
                            .filter(l -> l.getId().equals(lineDTO.getId()))
                            .findFirst()
                            .orElse(new PurchaseOrderLine());
                } else {
                    line = new PurchaseOrderLine();
                }

                if (lineDTO.getQuantity() != null) {
                    line.setQuantity(lineDTO.getQuantity());
                }
                if (lineDTO.getUnitPrice() != null) {
                    line.setUnitPrice(lineDTO.getUnitPrice());
                }
                if (lineDTO.getProductId() != null) {
                    Product product = productRepository.findById(lineDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with id: " + lineDTO.getProductId()));
                    line.setProduct(product);
                }

                line.setPurchaseOrder(existingPurchaseOrder);
                existingPurchaseOrder.getOrderLines().add(line);
            }

            existingPurchaseOrder.calculateTotalAmount();
        }

        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.save(existingPurchaseOrder);
        return purchaseOrderMapper.toDto(updatedPurchaseOrder);
    }

    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + id + " not found"));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot delete a delivered purchase order");
        }

        if (!purchaseOrder.getOrderLines().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete purchase order with existing lines. Delete all order lines first.");
        }

        purchaseOrderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ReadPurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

        return purchaseOrderRepository.findBySupplier(supplier)
                .stream()
                .map(purchaseOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void validateOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + orderId + " not found"));

        if (purchaseOrder.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING orders can be validated. Current status: " + purchaseOrder.getStatus());
        }

        purchaseOrder.setStatus(OrderStatus.VALIDATED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + orderId + " not found"));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Cannot cancel a delivered order");
        }

        if (purchaseOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Order is already cancelled");
        }

        purchaseOrder.setStatus(OrderStatus.CANCELLED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void receiveOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + orderId + " not found"));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Purchase order with ID " + orderId + " has already been received");
        }

        if (purchaseOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot receive a cancelled purchase order");
        }

        purchaseOrder.setStatus(OrderStatus.DELIVERED);
        LocalDate today = LocalDate.now();

        if (purchaseOrder.getOrderLines() != null && !purchaseOrder.getOrderLines().isEmpty()) {
            for (PurchaseOrderLine orderLine : purchaseOrder.getOrderLines()) {
                // Create a new StockLot for each order line
                StockLot stockLot = new StockLot();
                String lotNumber = generateLotNumber(orderId, orderLine.getId());
                stockLot.setLotNumber(lotNumber);
                stockLot.setEntryDate(today);

                Double quantity = orderLine.getQuantity();
                stockLot.setInitialQuantity(quantity);
                stockLot.setRemainingQuantity(quantity);

                stockLot.setPurchasePrice(orderLine.getUnitPrice());
                stockLot.setProduct(orderLine.getProduct());
                stockLot.setPurchaseOrderLine(orderLine);
                // Save stocklot
                StockLot savedStockLot = stockLotRepository.save(stockLot);

                // Create stock movement (IN)
                StockMovement stockMovement = new StockMovement();
                stockMovement.setMovementDate(today);
                stockMovement.setQuantity(quantity);
                stockMovement.setMovementType(StockMovementType.IN);
                stockMovement.setProduct(orderLine.getProduct());
                stockMovement.setStockLot(savedStockLot);
                stockMovement.setPurchasseOrderLine(orderLine);

                // Save stock movement
                stockMovementRepository.save(stockMovement);
            }
        }

        // Save
        purchaseOrderRepository.save(purchaseOrder);
    }

    /**
     * Generate a unique lot number for the stock lot
     * Format: LOT-ORDERID-LINEID-YYYYMMDD
     */
    private String generateLotNumber(Long orderId, Long lineId) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "LOT-" + orderId + "-" + lineId + "-" + date;
    }
}
