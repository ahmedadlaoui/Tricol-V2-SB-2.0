package com.example.tricolv2sb.Service;


import com.example.tricolv2sb.Entity.AuditLog;
import com.example.tricolv2sb.Repository.AuditLogRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService implements AuditLogServiceInterface {

    private final AuditLogRepository auditLogRepo;

    @Override
    public List<AuditLog> getAllLogs() {
        return List.of();
    }
}
