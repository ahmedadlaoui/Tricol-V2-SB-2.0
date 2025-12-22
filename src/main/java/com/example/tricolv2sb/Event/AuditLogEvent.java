package com.example.tricolv2sb.Event;

import com.example.tricolv2sb.Entity.Enum.ActionName;
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

    private ActionName action;

    private Map<String, Object> details;

    private Instant timestamp;


    public AuditLogEvent(String user, ActionName action, Map<String, Object> details) {
        this.user = user;
        this.action = action;
        this.details = details;
        this.timestamp = Instant.now();
    }
}
