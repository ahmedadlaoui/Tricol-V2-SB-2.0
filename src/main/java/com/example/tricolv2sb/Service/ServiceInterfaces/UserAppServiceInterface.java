package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;

import java.util.List;

public interface UserAppServiceInterface {
    void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO);

    List<ReadUserDTO> getAllUsers();
}
