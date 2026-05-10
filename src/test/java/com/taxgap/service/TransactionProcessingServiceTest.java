package com.taxgap.service;

import com.taxgap.dto.TransactionRequestDTO;
import com.taxgap.entity.Transaction;
import com.taxgap.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("Transaction Processing Service Tests")
class TransactionProcessingServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private TransactionValidationService validationService;
    
    @Mock
    private TaxCalculationService taxCalculationService;
    
    @Mock
    private com.taxgap.engine.RuleEngineService ruleEngineService;
    
    @Mock
    private ExceptionManagementService exceptionManagementService;
    
    @Mock
    private AuditLoggingService auditLoggingService;
    
    @InjectMocks
    private TransactionProcessingService processingService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Should process transaction with success")
    void testProcessTransactionSuccess() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now().minusDays(1))
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("180"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult validationResult = 
            new TransactionValidationService.ValidationResult(false, java.util.List.of());
        
        when(validationService.validateTransaction(any())).thenReturn(validationResult);
        
        TaxCalculationService.TaxCalculationResult taxResult = 
            new TaxCalculationService.TaxCalculationResult(
                new BigDecimal("180.00"),
                BigDecimal.ZERO,
                com.taxgap.entity.ComplianceStatus.COMPLIANT
            );
        
        when(taxCalculationService.calculateTax(any())).thenReturn(taxResult);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(ruleEngineService.executeAllActiveRules(any())).thenReturn(java.util.List.of());
        
        var response = processingService.processTransaction(request);
        
        assertNotNull(response);
        assertEquals("TXN001", response.getTransactionId());
        assertEquals("SUCCESS", response.getValidationStatus());
        assertEquals("COMPLIANT", response.getComplianceStatus());
        
        verify(transactionRepository, times(1)).save(any());
        verify(auditLoggingService, times(3)).logEvent(any(), any(), any());
    }
}
