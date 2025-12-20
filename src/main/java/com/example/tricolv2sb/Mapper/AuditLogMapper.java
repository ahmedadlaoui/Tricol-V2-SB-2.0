package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.auditlog.ReadAuditLogDTO;
import com.example.tricolv2sb.Entity.AuditLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "details", expression = "java(mapDetails(auditLog.getDetails(), objectMapper))")
    @Mapping(target = "timestamp", expression = "java(mapTimestamp(auditLog.getTimestamp()))")


    ReadAuditLogDTO toDto(AuditLog auditLog, @Context ObjectMapper objectMapper);


    default Map<String, Object> mapDetails(String detailsJson, ObjectMapper objectMapper) {
        Map<String, Object> detailsMap = new HashMap<>();
        if (detailsJson != null && !detailsJson.isEmpty()) {
            try {
                detailsMap = objectMapper.readValue(
                        detailsJson,
                        new TypeReference<Map<String, Object>>() {
                        });
            } catch (Exception e) {
                detailsMap.put("raw", detailsJson);
            }
        }
        return detailsMap;
    }

    default LocalDateTime mapTimestamp(java.time.Instant timestamp) {
        if (timestamp != null) {
            return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        }
        return null;
    }
}
