package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.PermissionDTO.CreatePermissionDTO;
import com.example.tricolv2sb.DTO.PermissionDTO.ReadPermissionDTO;

import java.util.List;

public interface CustomPermissionService {

    ReadPermissionDTO assignPermission(Long userId, CreatePermissionDTO dto);

    void revokePermission(Long userId, Long permissionId);

    List<ReadPermissionDTO> getUserCustomPermissions(Long userId);
}
