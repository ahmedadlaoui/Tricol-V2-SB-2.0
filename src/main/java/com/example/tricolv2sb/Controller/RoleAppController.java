package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.role.RoleDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.RoleAppServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleAppController {

    private final RoleAppServiceInterface roleAppService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRolesWithPermissions() {
        List<RoleDTO> roles = roleAppService.getAllRolesWithPermissions();
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles with permissions fetched successfully"));
    }
}

