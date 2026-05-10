package com.taxgap.service;

import com.taxgap.dto.TransactionRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("Transaction Validation Service Tests")
class TransactionValidationServiceTest {
    
    private TransactionValidationService validationService;
    
    @BeforeEach
    void setUp() {
        validationService = new TransactionValidationService();
    }
    
    @Test
    @DisplayName("Should validate a correct transaction")
    void testValidateCorrectTransaction() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now().minusDays(1))
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("180"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertFalse(result.hasErrors, "Validation should pass for correct transaction");
    }
    
    @Test
    @DisplayName("Should reject transaction with missing transaction ID")
    void testMissingTransactionId() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId(null)
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors, "Validation should fail for null transaction ID");
        assertTrue(result.errors.toString().contains("Transaction ID is required"));
    }
    
    @Test
    @DisplayName("Should reject transaction with zero amount")
    void testZeroAmount() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(BigDecimal.ZERO)
            .taxRate(new BigDecimal("0.18"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors);
        assertTrue(result.errors.toString().contains("Amount must be greater than 0"));
    }
    
    @Test
    @DisplayName("Should reject transaction with negative amount")
    void testNegativeAmount() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("-500"))
            .taxRate(new BigDecimal("0.18"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors);
    }
    
    @Test
    @DisplayName("Should reject transaction with future date")
    void testFutureDate() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now().plusDays(5))
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors);
        assertTrue(result.errors.toString().contains("cannot be in the future"));
    }
    
    @Test
    @DisplayName("Should reject invalid transaction type")
    void testInvalidTransactionType() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .transactionType("INVALID")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors);
        assertTrue(result.errors.toString().contains("Invalid transaction type"));
    }
    
    @Test
    @DisplayName("Should reject tax rate > 1")
    void testInvalidTaxRate() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("1.5"))
            .transactionType("SALE")
            .build();
        
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(dto);
        
        assertTrue(result.hasErrors);
        assertTrue(result.errors.toString().contains("Tax rate must be between 0 and 1"));
    }
}
