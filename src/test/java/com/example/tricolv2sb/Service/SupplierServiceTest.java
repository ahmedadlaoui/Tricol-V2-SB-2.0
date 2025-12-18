package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.supplier.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.supplier.ReadSupplierDTO;
import com.example.tricolv2sb.Entity.Supplier;
import com.example.tricolv2sb.Exception.BusinessViolationException;
import com.example.tricolv2sb.Exception.ResourceAlreadyExistsException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.SupplierMapper;
import com.example.tricolv2sb.Repository.SupplierRepository;
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
@DisplayName("Supplier Service Tests")
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private ReadSupplierDTO readSupplierDTO;
    private CreateSupplierDTO createSupplierDTO;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setIce("ICE123456789");
        supplier.setEmail("supplier@test.com");
        supplier.setPurchaseOrders(List.of());

        readSupplierDTO = new ReadSupplierDTO();
        readSupplierDTO.setId(1L);
        readSupplierDTO.setIce("ICE123456789");

        createSupplierDTO = new CreateSupplierDTO();
        createSupplierDTO.setIce("ICE123456789");
    }

    @Test
    @DisplayName("Fetch all suppliers returns list")
    void fetchAllSuppliers_ReturnsListOfSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(readSupplierDTO);

        List<ReadSupplierDTO> result = supplierService.fetchAllSuppliers();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Add supplier with unique ICE succeeds")
    void addSupplier_WithUniqueIce_CreatesSupplier() {
        when(supplierRepository.findByIce("ICE123456789")).thenReturn(Optional.empty());
        when(supplierMapper.toEntity(createSupplierDTO)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(readSupplierDTO);

        ReadSupplierDTO result = supplierService.addSupplier(createSupplierDTO);

        assertNotNull(result);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Add supplier with duplicate ICE throws exception")
    void addSupplier_WithDuplicateIce_ThrowsException() {
        when(supplierRepository.findByIce("ICE123456789")).thenReturn(Optional.of(supplier));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> supplierService.addSupplier(createSupplierDTO));
    }

    @Test
    @DisplayName("Fetch supplier when exists returns supplier")
    void fetchSupplier_WhenExists_ReturnsSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(readSupplierDTO);

        Optional<ReadSupplierDTO> result = supplierService.fetchSupplier(1L);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Delete supplier with no purchase orders succeeds")
    void deleteSupplier_WithNoPurchaseOrders_DeletesSuccessfully() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete supplier when not found throws exception")
    void deleteSupplier_WhenNotFound_ThrowsException() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> supplierService.deleteSupplier(1L));
    }
}
