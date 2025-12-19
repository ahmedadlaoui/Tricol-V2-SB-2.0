package com.example.tricolv2sb.Listener;

import com.example.tricolv2sb.Entity.AuditLog;
import com.example.tricolv2sb.Event.AuditLogEvent;
import com.example.tricolv2sb.Repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventListener {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;


    @Async
    @EventListener
    public void handle(AuditLogEvent logEvent) {

        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(logEvent.getUser());
            auditLog.setAction(logEvent.getAction());
            auditLog.setDetails(objectMapper.writeValueAsString(logEvent.getDetails()));
            auditLog.setTimestamp(Instant.now());
            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Failed to save audit log: {}", logEvent, e);
        }
    }
}
