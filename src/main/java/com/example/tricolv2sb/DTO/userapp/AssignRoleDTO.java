package com.example.tricolv2sb.DTO.userapp;

import com.example.tricolv2sb.Entity.Enum.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleDTO {
    @NotNull(message = "Role name is required")
    private RoleName roleName;
}








