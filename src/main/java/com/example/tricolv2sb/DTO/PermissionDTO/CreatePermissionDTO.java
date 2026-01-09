package com.example.tricolv2sb.DTO.PermissionDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionDTO {

    @NotNull(message = "Permission ID is required")
    private Long permissionId;

    @NotNull(message = "isGranted field is required")
    private Boolean isGranted;
}
