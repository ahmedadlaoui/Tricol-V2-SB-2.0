package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.supplier.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.supplier.ReadSupplierDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierServiceInterface {
    List<ReadSupplierDTO> fetchAllSuppliers();

    Optional<ReadSupplierDTO> fetchSupplier(Long id);

    ReadSupplierDTO addSupplier(CreateSupplierDTO dto);

    void deleteSupplier(Long id);

    ReadSupplierDTO updateSupplier(Long id, CreateSupplierDTO dto);
}
