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
            auditLogRepository.save(
                    AuditLog.builder()
                            .user(logEvent.getUser())
                            .action(logEvent.getAction())
                            .details(objectMapper.writeValueAsString(logEvent.getDetails()))
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to save audit log: {}", logEvent, e);
        }
    }
}
