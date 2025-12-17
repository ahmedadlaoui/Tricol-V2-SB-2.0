package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Repository.RoleAppRepository;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAppService implements UserAppServiceInterface {

    private final UserAppRepository userRepository;
    private final RoleAppRepository roleRepository;

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        RoleApp role = roleRepository.findByName(assignRoleDTO.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found: " + assignRoleDTO.getRoleName()));

        user.setRole(role);
        userRepository.save(user);
    }
}

