package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.DTO.product.UpdateProductDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.ProductInterface;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductInterface productService;
    private final StockServiceInterface stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    public ResponseEntity<ApiResponse<List<ReadProductDTO>>> getAllProducts() {
        List<ReadProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    public ResponseEntity<ApiResponse<ReadProductDTO>> getProductById(@PathVariable Long id) {
        ReadProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT:CREATE')")
    public ResponseEntity<ApiResponse<ReadProductDTO>> createProduct(
            @Valid @RequestBody CreateProductDTO createProductDTO) {
        ReadProductDTO createdProduct = productService.createProduct(createProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdProduct, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:UPDATE')")
    public ResponseEntity<ApiResponse<ReadProductDTO>> updateProduct(@PathVariable Long id,
            @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        ReadProductDTO updatedProduct = productService.updateProduct(id, updateProductDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @GetMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<ProductStockDetailDTO>> getProductStock(@PathVariable Long id) {
        ProductStockDetailDTO stock = stockService.getProductStockDetail(id);
        return ResponseEntity.ok(ApiResponse.success(stock, "Product stock detail fetched successfully"));
    }
}
