package com.example.tricolv2sb.Event;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class AuditLogEvent {

    private Long id;

    private String user;

    private String action;

    private Map<String, Object> details;

    private Instant timestamp;

}
