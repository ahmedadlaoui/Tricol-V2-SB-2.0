package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.supplier.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.supplier.ReadSupplierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SupplierServiceInterface {
    Page<ReadSupplierDTO> fetchAllSuppliers(Pageable pageable);

    Optional<ReadSupplierDTO> fetchSupplier(Long id);

    ReadSupplierDTO addSupplier(CreateSupplierDTO dto);

    void deleteSupplier(Long id);

    ReadSupplierDTO updateSupplier(Long id, CreateSupplierDTO dto);
}
