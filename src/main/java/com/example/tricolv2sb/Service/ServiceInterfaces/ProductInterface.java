package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.DTO.product.UpdateProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductInterface {

    Page<ReadProductDTO> getAllProducts(Pageable pageable);

    ReadProductDTO getProductById(Long id);

    ReadProductDTO createProduct(CreateProductDTO createProductDTO);

    ReadProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);

    void deleteProduct(Long id);
}
