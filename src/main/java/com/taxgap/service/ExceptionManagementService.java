package com.taxgap.service;

import com.taxgap.entity.TaxException;
import com.taxgap.entity.ExceptionSeverity;
import com.taxgap.repository.ExceptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ExceptionManagementService {
    
    private final ExceptionRepository exceptionRepository;
    
    public ExceptionManagementService(ExceptionRepository exceptionRepository) {
        this.exceptionRepository = exceptionRepository;
    }
    
    public void createException(String transactionId, Long customerId, String ruleName,
                              ExceptionSeverity severity, String message) {
        TaxException exception = TaxException.builder()
            .transactionId(transactionId)
            .customerId(customerId)
            .ruleName(ruleName)
            .severity(severity)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
        
        exceptionRepository.save(exception);
    }
}
