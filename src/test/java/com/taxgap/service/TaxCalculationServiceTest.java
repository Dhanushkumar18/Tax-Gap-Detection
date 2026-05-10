package com.taxgap.service;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TransactionType;
import com.taxgap.entity.ComplianceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("Tax Calculation Service Tests")
class TaxCalculationServiceTest {
    
    private TaxCalculationService taxCalculationService;
    
    @BeforeEach
    void setUp() {
        taxCalculationService = new TaxCalculationService();
    }
    
    @Test
    @DisplayName("Should calculate expected tax correctly")
    void testCalculateExpectedTax() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("180"))
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(new BigDecimal("180.00"), result.expectedTax);
    }
    
    @Test
    @DisplayName("Should mark as COMPLIANT when tax gap is within threshold")
    void testCompliantStatus() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("180"))
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(ComplianceStatus.COMPLIANT, result.complianceStatus);
    }
    
    @Test
    @DisplayName("Should mark as UNDERPAID when tax gap > threshold")
    void testUnderpaidStatus() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("100"))
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(ComplianceStatus.UNDERPAID, result.complianceStatus);
        assertTrue(result.taxGap.compareTo(new BigDecimal("1")) > 0);
    }
    
    @Test
    @DisplayName("Should mark as OVERPAID when tax gap < -threshold")
    void testOverpaidStatus() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("300"))
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(ComplianceStatus.OVERPAID, result.complianceStatus);
        assertTrue(result.taxGap.compareTo(new BigDecimal("-1")) < 0);
    }
    
    @Test
    @DisplayName("Should mark as NON_COMPLIANT when reported tax is missing")
    void testMissingReportedTax() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("1000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(null)
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(ComplianceStatus.NON_COMPLIANT, result.complianceStatus);
        assertNull(result.taxGap);
    }
    
    @Test
    @DisplayName("Should calculate tax with 5% tax rate")
    void testDifferentTaxRate() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("500"))
            .taxRate(new BigDecimal("0.05"))
            .reportedTax(new BigDecimal("25"))
            .transactionType(TransactionType.SALE)
            .build();
        
        TaxCalculationService.TaxCalculationResult result = 
            taxCalculationService.calculateTax(transaction);
        
        assertEquals(new BigDecimal("25.00"), result.expectedTax);
    }
}
