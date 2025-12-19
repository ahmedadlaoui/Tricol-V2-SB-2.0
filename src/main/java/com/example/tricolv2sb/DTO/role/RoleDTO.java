package com.example.tricolv2sb.DTO.role;

import com.example.tricolv2sb.Entity.Enum.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private RoleName name;
    private String description;
    private Set<PermissionDTO> permissions;
}
