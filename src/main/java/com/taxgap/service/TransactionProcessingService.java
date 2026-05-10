package com.taxgap.service;

import com.taxgap.dto.TransactionRequestDTO;
import com.taxgap.dto.TransactionResponseDTO;
import com.taxgap.entity.*;
import com.taxgap.repository.TransactionRepository;
import com.taxgap.engine.RuleEngineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionProcessingService {
    
    private final TransactionRepository transactionRepository;
    private final TransactionValidationService validationService;
    private final TaxCalculationService taxCalculationService;
    private final RuleEngineService ruleEngineService;
    private final ExceptionManagementService exceptionManagementService;
    private final AuditLoggingService auditLoggingService;
    private final ObjectMapper objectMapper;
    
    public TransactionProcessingService(
            TransactionRepository transactionRepository,
            TransactionValidationService validationService,
            TaxCalculationService taxCalculationService,
            RuleEngineService ruleEngineService,
            ExceptionManagementService exceptionManagementService,
            AuditLoggingService auditLoggingService,
            ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.validationService = validationService;
        this.taxCalculationService = taxCalculationService;
        this.ruleEngineService = ruleEngineService;
        this.exceptionManagementService = exceptionManagementService;
        this.auditLoggingService = auditLoggingService;
        this.objectMapper = objectMapper;
    }
    
    public List<TransactionResponseDTO> processTransactionBatch(List<TransactionRequestDTO> requests) {
        List<TransactionResponseDTO> responses = new ArrayList<>();
        
        for (TransactionRequestDTO request : requests) {
            TransactionResponseDTO response = processTransaction(request);
            responses.add(response);
        }
        
        return responses;
    }
    
    public TransactionResponseDTO processTransaction(TransactionRequestDTO request) {
        // Step 1: Validate transaction
        TransactionValidationService.ValidationResult validationResult = 
            validationService.validateTransaction(request);
        
        Transaction transaction = Transaction.builder()
            .transactionId(request.getTransactionId())
            .date(request.getDate())
            .customerId(request.getCustomerId())
            .amount(request.getAmount())
            .taxRate(request.getTaxRate())
            .reportedTax(request.getReportedTax())
            .transactionType(TransactionType.valueOf(request.getTransactionType()))
            .build();
        
        if (validationResult.hasErrors) {
            transaction.setValidationStatus(ValidationStatus.FAILURE);
            transaction.setFailureReasons(validationResult.getErrorMessage());
            transaction.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
            
            transactionRepository.save(transaction);
            auditLoggingService.logEvent(EventType.INGESTION, request.getTransactionId(),
                new AuditEventDetail("VALIDATION_FAILED", validationResult.getErrorMessage()));
            
            return mapToResponseDTO(transaction);
        }
        
        // Step 2: Mark as validation success
        transaction.setValidationStatus(ValidationStatus.SUCCESS);
        auditLoggingService.logEvent(EventType.INGESTION, request.getTransactionId(),
            new AuditEventDetail("VALIDATION_SUCCESS", "Transaction validated successfully"));
        
        // Step 3: Calculate taxes
        TaxCalculationService.TaxCalculationResult taxResult = 
            taxCalculationService.calculateTax(transaction);
        
        transaction.setExpectedTax(taxResult.expectedTax);
        transaction.setTaxGap(taxResult.taxGap);
        transaction.setComplianceStatus(taxResult.complianceStatus);
        
        auditLoggingService.logEvent(EventType.TAX_COMPUTATION, request.getTransactionId(),
            new AuditEventDetail("expectedTax", taxResult.expectedTax.toString(),
                "taxGap", taxResult.taxGap != null ? taxResult.taxGap.toString() : "null",
                "complianceStatus", taxResult.complianceStatus.toString()));
        
        // Step 4: Save transaction
        transaction = transactionRepository.save(transaction);
        
        // Step 5: Execute rules
        List<RuleEngineService.RuleViolation> violations = 
            ruleEngineService.executeAllActiveRules(transaction);
        
        for (RuleEngineService.RuleViolation violation : violations) {
            exceptionManagementService.createException(
                transaction.getTransactionId(),
                transaction.getCustomerId(),
                violation.ruleName,
                violation.severity,
                violation.message
            );
            
            auditLoggingService.logEvent(EventType.RULE_EXECUTION, request.getTransactionId(),
                new AuditEventDetail("ruleName", violation.ruleName, 
                    "violated", "true", "message", violation.message));
        }
        
        return mapToResponseDTO(transaction);
    }
    
    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
            .id(transaction.getId())
            .transactionId(transaction.getTransactionId())
            .date(transaction.getDate())
            .customerId(transaction.getCustomerId())
            .amount(transaction.getAmount())
            .taxRate(transaction.getTaxRate())
            .reportedTax(transaction.getReportedTax())
            .transactionType(transaction.getTransactionType().toString())
            .validationStatus(transaction.getValidationStatus().toString())
            .failureReasons(transaction.getFailureReasons())
            .expectedTax(transaction.getExpectedTax())
            .taxGap(transaction.getTaxGap())
            .complianceStatus(transaction.getComplianceStatus() != null ? 
                transaction.getComplianceStatus().toString() : null)
            .createdAt(transaction.getCreatedAt())
            .build();
    }
    
    private static class AuditEventDetail {
        private final String[] details;
        
        public AuditEventDetail(String... details) {
            this.details = details;
        }
        
        public Object toMap() {
            java.util.LinkedHashMap<String, String> map = new java.util.LinkedHashMap<>();
            for (int i = 0; i < details.length; i += 2) {
                if (i + 1 < details.length) {
                    map.put(details[i], details[i + 1]);
                }
            }
            return map;
        }
    }
}
