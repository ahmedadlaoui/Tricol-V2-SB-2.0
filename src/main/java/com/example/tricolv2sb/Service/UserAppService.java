package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.userapp.AssignRoleDTO;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.DTO.userapp.UserPermissionsDTO;
import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.Permission;
import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Entity.UserPermission;
import com.example.tricolv2sb.Exception.ResourceNotFoundException;
import com.example.tricolv2sb.Mapper.UserAppMapper;
import com.example.tricolv2sb.Repository.RoleAppRepository;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.UserAppServiceInterface;
import com.example.tricolv2sb.Util.interfaces.eventPublisherUtilInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAppService implements UserAppServiceInterface {

    private final UserAppRepository userRepository;
    private final RoleAppRepository roleRepository;
    private final UserAppMapper userAppMapper;
    private final eventPublisherUtilInterface eventPublisherUtilInterface;

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, AssignRoleDTO assignRoleDTO) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        RoleApp role = roleRepository.findByName(assignRoleDTO.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + assignRoleDTO.getRoleName()));
        user.setRole(role);
        userRepository.save(user);
        eventPublisherUtilInterface.triggerAuditLogEventPublisher(ActionName.ROLE_ASSIGNED, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadUserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userAppMapper::toReadUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPermissionsDTO getUserPermissions(Long userId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<String> rolePermissions = new ArrayList<>();
        String roleName = null;

        if (user.getRole() != null) {
            roleName = user.getRole().getName().name();
            if (user.getRole().getPermissions() != null) {
                rolePermissions = user.getRole().getPermissions().stream()
                        .map(Permission::getAuthority)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }

        List<UserPermissionsDTO.CustomPermissionDTO> customPermissions = new ArrayList<>();
        if (user.getCustomPermissions() != null) {
            customPermissions = user.getCustomPermissions().stream()
                    .map(up -> UserPermissionsDTO.CustomPermissionDTO.builder()
                            .permissionId(up.getPermission().getId())
                            .permissionCode(up.getPermission().getAuthority())
                            .isGranted(up.isGranted())
                            .build())
                    .collect(Collectors.toList());
        }

        Set<String> effectivePermissions = new HashSet<>(rolePermissions);
        if (user.getCustomPermissions() != null) {
            for (UserPermission up : user.getCustomPermissions()) {
                String code = up.getPermission().getAuthority();
                if (up.isGranted()) {
                    effectivePermissions.add(code);
                } else {
                    effectivePermissions.remove(code);
                }
            }
        }

        return UserPermissionsDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleName(roleName)
                .rolePermissions(rolePermissions)
                .customPermissions(customPermissions)
                .effectivePermissions(effectivePermissions.stream().sorted().collect(Collectors.toList()))
                .build();
    }
}
