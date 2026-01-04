package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.role.PermissionDTO;
import com.example.tricolv2sb.Entity.Permission;
import com.example.tricolv2sb.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        List<PermissionDTO> permissionDTOs = permissions.stream()
                .map(this::mapToPermissionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(permissionDTOs, "Permissions fetched successfully"));
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

