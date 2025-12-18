package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.DTO.product.UpdateProductDTO;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Exception.BusinessValidationException;
import com.example.tricolv2sb.Exception.ResourceAlreadyExistsException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.ProductMapper;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.ProductInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements ProductInterface {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ReadProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReadProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return productMapper.toDto(product);
    }

    public ReadProductDTO createProduct(CreateProductDTO createProductDTO) {
        if (productRepository.existsByReference(createProductDTO.getReference())) {
            throw new ResourceAlreadyExistsException(
                    "Product with reference '" + createProductDTO.getReference() + "' already exists");
        }

        Product product = productMapper.toEntity(createProductDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ReadProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        productMapper.updateEntity(updateProductDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (!product.getPurchaseOrderLines().isEmpty()) {
            throw new BusinessValidationException(
                    "Cannot delete product with existing order lines");
        }

        if (!product.getStockLots().isEmpty()) {
            throw new BusinessValidationException(
                    "Cannot delete product with existing stock");
        }

        productRepository.deleteById(id);
    }
}
