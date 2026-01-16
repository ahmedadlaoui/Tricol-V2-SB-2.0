package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogServiceInterface {

    Page<ReadAuditLogDTO> getAllLogs(Pageable pageable);

}
