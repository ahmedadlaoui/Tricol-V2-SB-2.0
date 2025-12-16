package com.example.tricolv2sb.Entity;

import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.Enum.RessourceName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "permission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ressource", "action"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RessourceName ressource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionName action;

    @Column(length = 500)
    private String description;

    public String getAuthority() {
        return ressource.name() + ":" + action.name();
    }
}