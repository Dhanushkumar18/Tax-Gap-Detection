package com.taxgap.service;

import com.taxgap.entity.AuditLog;
import com.taxgap.entity.EventType;
import com.taxgap.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditLoggingService {
    
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    
    public AuditLoggingService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }
    
    public void logEvent(EventType eventType, String transactionId, Object details) {
        try {
            String detailJson = objectMapper.writeValueAsString(details);
            
            AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .detailJson(detailJson)
                .build();
            
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Log but don't fail the transaction
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
}
