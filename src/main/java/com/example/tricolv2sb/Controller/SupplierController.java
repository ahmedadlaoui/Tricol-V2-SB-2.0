package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.supplier.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.supplier.ReadSupplierDTO;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Service.ServiceInterfaces.SupplierServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierServiceInterface supplierService;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER:READ')")
    public ResponseEntity<ApiResponse<List<ReadSupplierDTO>>> getAllSuppliers() {
        List<ReadSupplierDTO> suppliers = supplierService.fetchAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers, "Suppliers fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPPLIER:CREATE')")
    public ResponseEntity<ApiResponse<ReadSupplierDTO>> createSupplier(@Valid @RequestBody CreateSupplierDTO dto) {
        ReadSupplierDTO newSupplier = supplierService.addSupplier(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(newSupplier, "Supplier created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER:READ')")
    public ResponseEntity<ApiResponse<ReadSupplierDTO>> getSupplierById(@PathVariable Long id) {
        return supplierService.fetchSupplier(id)
                .map(supplierDTO -> ResponseEntity
                        .ok(ApiResponse.success(supplierDTO, "Supplier fetched successfully")))
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER:UPDATE')")
    public ResponseEntity<ApiResponse<ReadSupplierDTO>> updateSupplier(@PathVariable Long id,
            @Valid @RequestBody CreateSupplierDTO dto) {
        ReadSupplierDTO updatedSupplier = supplierService.updateSupplier(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedSupplier, "Supplier updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER:DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Supplier deleted successfully"));
    }
}
