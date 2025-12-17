package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class PurchaseOrderController {

    private final PurchaseOrderInterface purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<ReadPurchaseOrderDTO>> getAllPurchaseOrders() {
        List<ReadPurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(purchaseOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadPurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id) {
        ReadPurchaseOrderDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(purchaseOrder);
    }

    @PostMapping
    public ResponseEntity<ReadPurchaseOrderDTO> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        ReadPurchaseOrderDTO createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(createPurchaseOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchaseOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadPurchaseOrderDTO> updatePurchaseOrder(@PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrderDTO) {
        ReadPurchaseOrderDTO updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id,
                updatePurchaseOrderDTO);
        return ResponseEntity.ok(updatedPurchaseOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/supplier/{id}")
    public ResponseEntity<List<ReadPurchaseOrderDTO>> getPurchaseOrdersBySupplier(@PathVariable Long id) {
        List<ReadPurchaseOrderDTO> purchaseOrders = purchaseOrderService.getPurchaseOrdersBySupplier(id);
        return ResponseEntity.ok(purchaseOrders);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<String> validateOrder(@PathVariable Long id) {
        purchaseOrderService.validateOrder(id);
        return ResponseEntity.ok("Purchase order " + id + " has been validated");
    }

    @PutMapping("/{id}/reception")
    public ResponseEntity<String> receiveOrder(@PathVariable Long id) {
        purchaseOrderService.receiveOrder(id);
        return ResponseEntity.ok("Purchase order " + id + " has been successfully received and stock lots created");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return ResponseEntity.ok("Purchase order " + id + " has been cancelled");
    }
}
