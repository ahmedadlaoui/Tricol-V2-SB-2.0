package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.supplier.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.supplier.ReadSupplierDTO;
import com.example.tricolv2sb.Entity.Supplier;
import com.example.tricolv2sb.Exception.BusinessValidationException;
import com.example.tricolv2sb.Exception.ResourceAlreadyExistsException;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.SupplierMapper;
import com.example.tricolv2sb.Repository.SupplierRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.SupplierServiceInterface;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceInterface {

    private final SupplierMapper supplierMapper;
    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<ReadSupplierDTO> fetchAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @Transactional
    public ReadSupplierDTO addSupplier(CreateSupplierDTO dto) {
        supplierRepository.findByIce(dto.getIce())
                .ifPresent(s -> {
                    throw new ResourceAlreadyExistsException(
                            "Supplier with ICE '" + dto.getIce() + "' already exists");
                });

        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Transactional(readOnly = true)
    public Optional<ReadSupplierDTO> fetchSupplier(Long id) {
        return Optional.of(
                supplierRepository.findById(id)
                        .map(supplierMapper::toDto)
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id)));
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        if (!supplier.getPurchaseOrders().isEmpty()) {
            throw new BusinessValidationException(
                    "Cannot delete supplier with existing purchase orders");
        }

        supplierRepository.deleteById(id);
    }

    @Transactional
    public ReadSupplierDTO updateSupplier(Long id, CreateSupplierDTO dto) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        supplierMapper.updateFromDto(dto, existingSupplier);
        Supplier savedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDto(savedSupplier);
    }
}
