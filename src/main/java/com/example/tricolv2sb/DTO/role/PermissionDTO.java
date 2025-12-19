package com.example.tricolv2sb.DTO.role;

import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.Enum.RessourceName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private RessourceName ressource;
    private ActionName action;
    private String description;
    private String authority; // e.g., "STOCK:READ"
}

