package com.example.tricolv2sb.DTO.PermissionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPermissionDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private Long permissionId;
    private String permissionCode;
    private String permissionDescription;
    private boolean isGranted;
    private LocalDateTime grantedAt;
    private String grantedBy;
}
