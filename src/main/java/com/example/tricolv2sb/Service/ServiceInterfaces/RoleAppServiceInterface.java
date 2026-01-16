package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.role.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleAppServiceInterface {
    Page<RoleDTO> getAllRolesWithPermissions(Pageable pageable);
}
