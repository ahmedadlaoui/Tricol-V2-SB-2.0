package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /**
     * GET /api/v1/stock/mouvements
     * 
     * Exemples d'utilisation :
     * - GET /api/v1/stock/mouvements?produitId=123&type=SORTIE&page=0&size=10
     * - GET /api/v1/stock/mouvements?dateDebut=2025-01-01&dateFin=2025-03-31
     * - GET /api/v1/stock/mouvements?numeroLot=LOT-2025-001
     * - GET /api/v1/stock/mouvements?reference=PROD001&type=ENTREE&dateDebut=2025-01-01&page=0&size=20
     */
    @GetMapping("/mouvements")
    public ResponseEntity<Page<ReadStockMovementDTO>> searchStockMovements(
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

        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/v1/stock/mouvements/all (ancien endpoint maintenu pour
     * compatibilité)
     * Gets a list of all stock movements
     */
    @GetMapping("/mouvements/all")
    public ResponseEntity<List<ReadStockMovementDTO>> getAllStockMovements() {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchAllStockMovements();
        return ResponseEntity.ok(movements);
    }

    /**
     * GET /api/v1/stock/mouvements/{id}
     * Gets a single stock movement by its ID
     */
    @GetMapping("/mouvements/{id}")
    public ResponseEntity<ReadStockMovementDTO> getStockMovementById(@PathVariable Long id) {
        return stockMovementService.fetchStockMovementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * GET /api/v1/stock/mouvements/product/{productId}
     * Gets all stock movements for a specific product
     */
    @GetMapping("/mouvements/product/{productId}")
    public ResponseEntity<List<ReadStockMovementDTO>> getStockMovementsByProduct(@PathVariable Long productId) {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchStockMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }
}
