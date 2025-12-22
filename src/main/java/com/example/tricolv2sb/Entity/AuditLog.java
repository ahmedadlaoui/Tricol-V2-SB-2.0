package com.example.tricolv2sb.Entity;


import com.example.tricolv2sb.Entity.Enum.ActionName;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Table(name = "audit_log")
@RequiredArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;

    @Enumerated(EnumType.STRING)
    private ActionName action;

    @Column(columnDefinition = "JSON")
    private String details;

    private Instant timestamp;

}
