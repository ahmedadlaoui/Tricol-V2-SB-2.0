package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;

import java.util.List;

public interface AuditLogServiceInterface {

    List<ReadAuditLogDTO> getAllLogs();

}
