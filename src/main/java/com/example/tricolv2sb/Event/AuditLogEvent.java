package com.example.tricolv2sb.Event;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data

public class AuditLogEvent {

    private String user;

    private String action;

    private Map<String, Object> details;

    private Instant timestamp;


    public AuditLogEvent(String user, String action, Map<String, Object> details) {
        this.user = user;
        this.action = action;
        this.details = details;
        this.timestamp = Instant.now();
    }
}
