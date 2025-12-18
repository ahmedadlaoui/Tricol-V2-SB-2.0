package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.stock.ReadStockMovementDTO;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping("/mouvements")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT:READ')")
    public ResponseEntity<ApiResponse<Page<ReadStockMovementDTO>>> searchStockMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) StockMovementType type,
            @RequestParam(required = false) String numeroLot,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("movementDate").descending());

        Page<ReadStockMovementDTO> result = stockMovementService.searchStockMovements(
                dateDebut, dateFin, produitId, reference, type, numeroLot, pageable);

        return ResponseEntity.ok(ApiResponse.success(result, "Stock movements searched successfully"));
    }

    @GetMapping("/mouvements/all")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT:READ')")
    public ResponseEntity<ApiResponse<List<ReadStockMovementDTO>>> getAllStockMovements() {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchAllStockMovements();
        return ResponseEntity.ok(ApiResponse.success(movements, "All stock movements fetched successfully"));
    }

    @GetMapping("/mouvements/{id}")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT:READ')")
    public ResponseEntity<ApiResponse<ReadStockMovementDTO>> getStockMovementById(@PathVariable Long id) {
        return stockMovementService.fetchStockMovementById(id)
                .map(res -> ResponseEntity.ok(ApiResponse.success(res, "Stock movement fetched successfully")))
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with ID: " + id));
    }

    @GetMapping("/mouvements/product/{productId}")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT:READ')")
    public ResponseEntity<ApiResponse<List<ReadStockMovementDTO>>> getStockMovementsByProduct(
            @PathVariable Long productId) {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchStockMovementsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(movements, "Stock movements for product fetched successfully"));
    }
}
