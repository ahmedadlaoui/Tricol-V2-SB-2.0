package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogServiceInterface auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_LOGS:READ')")
    public ResponseEntity<ApiResponse<Page<ReadAuditLogDTO>>> fetchAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReadAuditLogDTO> logs = auditLogService.getAllLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs fetched successfully"));
    }

}
