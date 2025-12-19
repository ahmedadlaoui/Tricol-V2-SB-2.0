package com.example.tricolv2sb.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "audit_log")
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
