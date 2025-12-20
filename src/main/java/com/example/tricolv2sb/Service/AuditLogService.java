package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import com.example.tricolv2sb.Entity.AuditLog;
import com.example.tricolv2sb.Mapper.AuditLogMapper;
import com.example.tricolv2sb.Repository.AuditLogRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService implements AuditLogServiceInterface {

    private final AuditLogRepository auditLogRepo;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<ReadAuditLogDTO> getAllLogs() {
        List<AuditLog> logs = auditLogRepo.findAll();
        return logs.stream()
                .map(log -> auditLogMapper.toDto(log, objectMapper))
                .collect(Collectors.toList());
    }
}
