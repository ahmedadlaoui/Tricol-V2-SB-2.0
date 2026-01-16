package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.DTO.userapp.UserPermissionsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAppServiceInterface {
    void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO);

    Page<ReadUserDTO> getAllUsers(Pageable pageable);

    UserPermissionsDTO getUserPermissions(Long userId);
}
