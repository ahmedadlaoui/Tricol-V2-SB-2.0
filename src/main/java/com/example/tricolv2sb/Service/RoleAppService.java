package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.role.PermissionDTO;
import com.example.tricolv2sb.DTO.role.RoleDTO;
import com.example.tricolv2sb.Entity.Permission;
import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Repository.RoleAppRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.RoleAppServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAppService implements RoleAppServiceInterface {

    private final RoleAppRepository roleAppRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRolesWithPermissions() {
        // Fetch all roles with their permissions eagerly loaded
        List<RoleApp> roles = roleAppRepository.findAllWithPermissions();

        return roles.stream()
                .map(this::mapToRoleDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO mapToRoleDTO(RoleApp role) {
        Set<PermissionDTO> permissionDTOs = null;

        if (role.getPermissions() != null) {
            permissionDTOs = role.getPermissions().stream()
                    .map(this::mapToPermissionDTO)
                    .collect(Collectors.toSet());
        }

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionDTOs)
                .build();
    }

    private PermissionDTO mapToPermissionDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .ressource(permission.getRessource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .authority(permission.getAuthority())
                .build();
    }
}

