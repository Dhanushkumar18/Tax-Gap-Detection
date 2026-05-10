package com.taxgap.repository;

import com.taxgap.entity.AuditLog;
import com.taxgap.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTransactionId(String transactionId);
    
    List<AuditLog> findByEventType(EventType eventType);
}
