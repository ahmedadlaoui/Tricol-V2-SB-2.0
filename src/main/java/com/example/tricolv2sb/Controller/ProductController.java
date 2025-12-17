package com.example.tricolv2sb.Controller;


import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.DTO.product.UpdateProductDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.ProductInterface;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ReadProductDTO>> getAllProducts() {
        List<ReadProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadProductDTO> getProductById(@PathVariable Long id) {
        ReadProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ReadProductDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        ReadProductDTO createdProduct = productService.createProduct(createProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        ReadProductDTO updatedProduct = productService.updateProduct(id, updateProductDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<ProductStockDetailDTO> getProductStock(@PathVariable Long id) {
        ProductStockDetailDTO stock = stockService.getProductStockDetail(id);
        return ResponseEntity.ok(stock);
    }
}
