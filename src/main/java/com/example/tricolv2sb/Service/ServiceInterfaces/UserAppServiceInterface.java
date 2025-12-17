package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;

public interface UserAppServiceInterface {
    void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO);
}

