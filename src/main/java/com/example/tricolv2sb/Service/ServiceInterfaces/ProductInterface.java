package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.DTO.product.UpdateProductDTO;

import java.util.List;

public interface ProductInterface {
    
    List<ReadProductDTO> getAllProducts();
    
    ReadProductDTO getProductById(Long id);
    
    ReadProductDTO createProduct(CreateProductDTO createProductDTO);
    
    ReadProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);
    
    void deleteProduct(Long id);
}
