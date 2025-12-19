package com.example.tricolv2sb.Entity;


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

    private String action;

    @Column(columnDefinition = "JSON")
    private String details;

    private Instant timestamp;

}
