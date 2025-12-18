package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class PurchaseOrderController {

    private final PurchaseOrderInterface purchaseOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:READ')")
    public ResponseEntity<ApiResponse<List<ReadPurchaseOrderDTO>>> getAllPurchaseOrders() {
        List<ReadPurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success(purchaseOrders, "Purchase orders fetched successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:READ')")
    public ResponseEntity<ApiResponse<ReadPurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        ReadPurchaseOrderDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(purchaseOrder, "Purchase order fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:CREATE')")
    public ResponseEntity<ApiResponse<ReadPurchaseOrderDTO>> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderDTO createPurchaseOrderDTO) {
        ReadPurchaseOrderDTO createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(createPurchaseOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdPurchaseOrder, "Purchase order created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:UPDATE')")
    public ResponseEntity<ApiResponse<ReadPurchaseOrderDTO>> updatePurchaseOrder(@PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrderDTO) {
        ReadPurchaseOrderDTO updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id,
                updatePurchaseOrderDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPurchaseOrder, "Purchase order updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:DELETE')")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase order deleted successfully"));
    }

    @GetMapping("/supplier/{id}")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:READ')")
    public ResponseEntity<ApiResponse<List<ReadPurchaseOrderDTO>>> getPurchaseOrdersBySupplier(@PathVariable Long id) {
        List<ReadPurchaseOrderDTO> purchaseOrders = purchaseOrderService.getPurchaseOrdersBySupplier(id);
        return ResponseEntity
                .ok(ApiResponse.success(purchaseOrders, "Purchase orders for supplier fetched successfully"));
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:VALIDATE')")
    public ResponseEntity<ApiResponse<Void>> validateOrder(@PathVariable Long id) {
        purchaseOrderService.validateOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase order validated successfully"));
    }

    @PutMapping("/{id}/reception")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:RECEIVE')")
    public ResponseEntity<ApiResponse<Void>> receiveOrder(@PathVariable Long id) {
        purchaseOrderService.receiveOrder(id);
        return ResponseEntity
                .ok(ApiResponse.success(null, "Purchase order received and stock lots created successfully"));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('PUCHASE_ORDER:CANCEL')")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase order cancelled successfully"));
    }
}
