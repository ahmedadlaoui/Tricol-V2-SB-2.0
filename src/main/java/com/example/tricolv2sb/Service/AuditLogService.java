package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import com.example.tricolv2sb.Entity.AuditLog;
import com.example.tricolv2sb.Repository.AuditLogRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService implements AuditLogServiceInterface {

    private final AuditLogRepository auditLogRepo;
    private final ObjectMapper objectMapper;

    @Override
    public List<ReadAuditLogDTO> getAllLogs() {
        List<AuditLog> logs = auditLogRepo.findAll();
        return logs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReadAuditLogDTO mapToDTO(AuditLog auditLog) {
        // Parse details JSON string to Map
        Map<String, Object> detailsMap = new HashMap<>();
        if (auditLog.getDetails() != null && !auditLog.getDetails().isEmpty()) {
            try {
                detailsMap = objectMapper.readValue(auditLog.getDetails(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                log.warn("Failed to parse audit log details for log ID {}: {}", auditLog.getId(), e.getMessage());
                // If parsing fails, return empty map or keep as string in a wrapper
                detailsMap.put("raw", auditLog.getDetails());
            }
        }

        // Convert Instant to LocalDateTime
        LocalDateTime timestamp = null;
        if (auditLog.getTimestamp() != null) {
            timestamp = LocalDateTime.ofInstant(auditLog.getTimestamp(), ZoneId.systemDefault());
        }

        return ReadAuditLogDTO.builder()
                .id(auditLog.getId())
                .user(auditLog.getUser())
                .action(auditLog.getAction())
                .details(detailsMap)
                .timestamp(timestamp)
                .build();
    }
}
