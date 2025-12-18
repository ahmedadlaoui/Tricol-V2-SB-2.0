package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.product.CreateProductDTO;
import com.example.tricolv2sb.DTO.product.ReadProductDTO;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Exception.ResourceAlreadyExistsException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.ProductMapper;
import com.example.tricolv2sb.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ReadProductDTO readProductDTO;
    private CreateProductDTO createProductDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setReference("PROD-001");
        product.setName("Test Product");
        product.setUnitPrice(100);
        product.setCategory("Electronics");
        product.setReorderPoint(10.0);
        product.setUnitOfMeasure("UNIT");

        readProductDTO = new ReadProductDTO();
        readProductDTO.setId(1L);
        readProductDTO.setReference("PROD-001");
        readProductDTO.setName("Test Product");

        createProductDTO = new CreateProductDTO();
        createProductDTO.setReference("PROD-001");
        createProductDTO.setName("Test Product");
    }

    @Test
    @DisplayName("Get all products returns list")
    void getAllProducts_ReturnsListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(readProductDTO);

        List<ReadProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("PROD-001", result.get(0).getReference());
    }

    @Test
    @DisplayName("Get product by ID when exists returns product")
    void getProductById_WhenExists_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(readProductDTO);

        ReadProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("PROD-001", result.getReference());
    }

    @Test
    @DisplayName("Get product by ID when not exists throws exception")
    void getProductById_WhenNotExists_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    @DisplayName("Create product with unique reference succeeds")
    void createProduct_WithUniqueReference_CreatesProduct() {
        when(productRepository.existsByReference("PROD-001")).thenReturn(false);
        when(productMapper.toEntity(createProductDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(readProductDTO);

        ReadProductDTO result = productService.createProduct(createProductDTO);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product with duplicate reference throws exception")
    void createProduct_WithDuplicateReference_ThrowsException() {
        when(productRepository.existsByReference("PROD-001")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> productService.createProduct(createProductDTO));
    }

    @Test
    @DisplayName("Delete product with no relations succeeds")
    void deleteProduct_WhenNoRelations_DeletesSuccessfully() {
        product.setPurchaseOrderLines(List.of());
        product.setStockLots(List.of());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
}
