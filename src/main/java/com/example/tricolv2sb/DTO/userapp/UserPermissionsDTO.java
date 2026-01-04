package com.example.tricolv2sb.DTO.userapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsDTO {
    private Long userId;
    private String email;
    private String fullName;
    private String roleName;
    private List<String> rolePermissions;
    private List<CustomPermissionDTO> customPermissions;
    private List<String> effectivePermissions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomPermissionDTO {
        private Long permissionId;
        private String permissionCode;
        private boolean isGranted;
    }
}
