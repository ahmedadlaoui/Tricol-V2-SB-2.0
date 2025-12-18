package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.BusinessValidationException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.PurchaseOrderMapper;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.PurchaseOrderRepository;
import com.example.tricolv2sb.Repository.StockLotRepository;
import com.example.tricolv2sb.Repository.StockMovementRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    public ReadPurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        Supplier supplier = supplierRepository.findById(createPurchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with ID: " + createPurchaseOrderDTO.getSupplierId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setStatus(OrderStatus.PENDING);
        purchaseOrder.setSupplier(supplier);

        List<PurchaseOrderLine> orderLines = new ArrayList<>();
        for (CreatePurchaseOrderDTO.OrderLineDTO lineDTO : createPurchaseOrderDTO.getOrderLines()) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + lineDTO.getProductId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));

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
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Product not found with ID: " + lineDTO.getProductId()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessValidationException("Cannot delete a delivered purchase order");
        }

        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Transactional(readOnly = true)
    public List<ReadPurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));

        return purchaseOrderRepository.findBySupplier(supplier)
                .stream()
                .map(purchaseOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void validateOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + orderId));

        if (purchaseOrder.getStatus() != OrderStatus.PENDING) {
            throw new BusinessValidationException(
                    "Only PENDING orders can be validated. Current status: " + purchaseOrder.getStatus());
        }

        purchaseOrder.setStatus(OrderStatus.VALIDATED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + orderId));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessValidationException("Cannot cancel a delivered order");
        }

        if (purchaseOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException("Order is already cancelled");
        }

        purchaseOrder.setStatus(OrderStatus.CANCELLED);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void receiveOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + orderId));

        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessValidationException("Purchase order has already been received");
        }

        if (purchaseOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessValidationException("Cannot receive a cancelled purchase order");
        }

        purchaseOrder.setStatus(OrderStatus.DELIVERED);
        LocalDate today = LocalDate.now();

        if (purchaseOrder.getOrderLines() != null && !purchaseOrder.getOrderLines().isEmpty()) {
            for (PurchaseOrderLine orderLine : purchaseOrder.getOrderLines()) {
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
                StockLot savedStockLot = stockLotRepository.save(stockLot);

                StockMovement stockMovement = new StockMovement();
                stockMovement.setMovementDate(today);
                stockMovement.setQuantity(quantity);
                stockMovement.setMovementType(StockMovementType.IN);
                stockMovement.setProduct(orderLine.getProduct());
                stockMovement.setStockLot(savedStockLot);
                stockMovement.setPurchasseOrderLine(orderLine);

                stockMovementRepository.save(stockMovement);
            }
        }

        purchaseOrderRepository.save(purchaseOrder);
    }

    private String generateLotNumber(Long orderId, Long lineId) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "LOT-" + orderId + "-" + lineId + "-" + date;
    }
}
