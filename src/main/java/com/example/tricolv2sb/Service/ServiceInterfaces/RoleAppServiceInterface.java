package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.role.RoleDTO;

import java.util.List;

public interface RoleAppServiceInterface {
    List<RoleDTO> getAllRolesWithPermissions();
}
