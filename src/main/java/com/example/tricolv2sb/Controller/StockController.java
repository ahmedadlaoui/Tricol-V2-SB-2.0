package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.product.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.stock.StockSummaryDTO;
import com.example.tricolv2sb.DTO.stock.StockValuationDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockController {

    private final StockServiceInterface stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<List<StockSummaryDTO>>> getGlobalStock() {
        List<StockSummaryDTO> stock = stockService.getGlobalStock();
        return ResponseEntity.ok(ApiResponse.success(stock, "Global stock summary fetched successfully"));
    }

    @GetMapping("/product/{id}")
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<ProductStockDetailDTO>> getProductStockDetail(@PathVariable Long id) {
        ProductStockDetailDTO detail = stockService.getProductStockDetail(id);
        return ResponseEntity.ok(ApiResponse.success(detail, "Product stock detail fetched successfully"));
    }

    @GetMapping("/valuation")
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<StockValuationDTO>> getTotalValuation() {
        StockValuationDTO valuation = stockService.getTotalValuation();
        return ResponseEntity.ok(ApiResponse.success(valuation, "Stock valuation fetched successfully"));
    }

}
