package com.example.tricolv2sb.Controller;


import com.example.tricolv2sb.Service.ServiceInterfaces.AuditLogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogServiceInterface auditLogService;

    @GetMapping()

}
