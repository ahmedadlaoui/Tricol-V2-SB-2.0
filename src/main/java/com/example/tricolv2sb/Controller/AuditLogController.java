package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogServiceInterface auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_LOGS:READ')")
    public ResponseEntity<ApiResponse<List<ReadAuditLogDTO>>> fetchAuditLogs() {
        List<ReadAuditLogDTO> logs = auditLogService.getAllLogs();
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs fetched successfully"));
    }

}
