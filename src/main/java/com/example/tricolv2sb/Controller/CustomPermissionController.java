package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.PermissionDTO.CreatePermissionDTO;
import com.example.tricolv2sb.DTO.PermissionDTO.ReadPermissionDTO;
import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.Service.ServiceInterfaces.CustomPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/permissions")
@RequiredArgsConstructor
public class CustomPermissionController {

        private final CustomPermissionService customPermissionService;

        @PostMapping
        @PreAuthorize("hasAuthority('USER:UPDATE')")
        public ResponseEntity<ApiResponse<ReadPermissionDTO>> assignPermission(
                        @PathVariable Long userId,
                        @Valid @RequestBody CreatePermissionDTO dto) {
                ReadPermissionDTO result = customPermissionService.assignPermission(userId, dto);
                return ResponseEntity.ok(ApiResponse.success(result, "Permission assigned successfully"));
        }

        @GetMapping
        @PreAuthorize("hasAuthority('USER:READ')")
        public ResponseEntity<ApiResponse<List<ReadPermissionDTO>>> getUserCustomPermissions(
                        @PathVariable Long userId) {
                List<ReadPermissionDTO> permissions = customPermissionService.getUserCustomPermissions(userId);
                return ResponseEntity.ok(ApiResponse.success(permissions, "Custom permissions fetched successfully"));
        }

        @DeleteMapping("/{permissionId}")
        @PreAuthorize("hasAuthority('USER:UPDATE')")
        public ResponseEntity<ApiResponse<Void>> revokePermission(
                        @PathVariable Long userId,
                        @PathVariable Long permissionId) {
                customPermissionService.revokePermission(userId, permissionId);
                return ResponseEntity.ok(ApiResponse.success(null, "Permission revoked successfully"));
        }
}
