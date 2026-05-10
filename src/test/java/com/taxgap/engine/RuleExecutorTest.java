package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TransactionType;
import com.taxgap.entity.TaxRule;
import com.taxgap.entity.ExceptionSeverity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@DisplayName("High Value Transaction Rule Tests")
class HighValueTransactionRuleExecutorTest {
    
    private HighValueTransactionRuleExecutor executor;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        executor = new HighValueTransactionRuleExecutor(objectMapper);
    }
    
    @Test
    @DisplayName("Should flag transaction exceeding threshold")
    void testTransactionExceedsThreshold() {
        String config = "{\"threshold\": \"50000\"}";
        TaxRule rule = TaxRule.builder()
            .ruleName("HighValueTransactionRule")
            .description("Test rule")
            .ruleConfig(config)
            .enabled(true)
            .defaultSeverity(ExceptionSeverity.HIGH)
            .build();
        
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("100000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("18000"))
            .transactionType(TransactionType.SALE)
            .build();
        
        RuleExecutionResult result = executor.execute(transaction, rule);
        
        assertTrue(result.violated, "Should flag transaction exceeding threshold");
    }
    
    @Test
    @DisplayName("Should not flag transaction within threshold")
    void testTransactionWithinThreshold() {
        String config = "{\"threshold\": \"100000\"}";
        TaxRule rule = TaxRule.builder()
            .ruleName("HighValueTransactionRule")
            .description("Test rule")
            .ruleConfig(config)
            .enabled(true)
            .defaultSeverity(ExceptionSeverity.HIGH)
            .build();
        
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("50000"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("9000"))
            .transactionType(TransactionType.SALE)
            .build();
        
        RuleExecutionResult result = executor.execute(transaction, rule);
        
        assertFalse(result.violated, "Should not flag transaction within threshold");
    }
    
    @Test
    @DisplayName("Should flag transaction exactly at threshold plus 1")
    void testTransactionAtThresholdPlus1() {
        String config = "{\"threshold\": \"50000\"}";
        TaxRule rule = TaxRule.builder()
            .ruleName("HighValueTransactionRule")
            .description("Test rule")
            .ruleConfig(config)
            .enabled(true)
            .defaultSeverity(ExceptionSeverity.HIGH)
            .build();
        
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("50001"))
            .taxRate(new BigDecimal("0.18"))
            .reportedTax(new BigDecimal("9000"))
            .transactionType(TransactionType.SALE)
            .build();
        
        RuleExecutionResult result = executor.execute(transaction, rule);
        
        assertTrue(result.violated, "Should flag transaction exceeding threshold");
    }
}

@DisplayName("GST Slab Violation Rule Tests")
class GSTSlabViolationRuleExecutorTest {
    
    private GSTSlabViolationRuleExecutor executor;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        executor = new GSTSlabViolationRuleExecutor(objectMapper);
    }
    
    @Test
    @DisplayName("Should flag when amount exceeds slab but tax rate is lower than required")
    void testGSTSlabViolation() {
        String config = "{\"slabThreshold\": \"50000\", \"requiredRate\": \"0.18\"}";
        TaxRule rule = TaxRule.builder()
            .ruleName("GSTSlabViolationRule")
            .description("Test rule")
            .ruleConfig(config)
            .enabled(true)
            .defaultSeverity(ExceptionSeverity.HIGH)
            .build();
        
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("100000"))
            .taxRate(new BigDecimal("0.05"))
            .reportedTax(new BigDecimal("5000"))
            .transactionType(TransactionType.SALE)
            .build();
        
        RuleExecutionResult result = executor.execute(transaction, rule);
        
        assertTrue(result.violated, "Should flag GST slab violation");
    }
    
    @Test
    @DisplayName("Should not flag when amount is below slab")
    void testAmountBelowSlab() {
        String config = "{\"slabThreshold\": \"50000\", \"requiredRate\": \"0.18\"}";
        TaxRule rule = TaxRule.builder()
            .ruleName("GSTSlabViolationRule")
            .description("Test rule")
            .ruleConfig(config)
            .enabled(true)
            .defaultSeverity(ExceptionSeverity.HIGH)
            .build();
        
        Transaction transaction = Transaction.builder()
            .transactionId("TXN001")
            .date(LocalDate.now())
            .customerId(1L)
            .amount(new BigDecimal("10000"))
            .taxRate(new BigDecimal("0.05"))
            .reportedTax(new BigDecimal("500"))
            .transactionType(TransactionType.SALE)
            .build();
        
        RuleExecutionResult result = executor.execute(transaction, rule);
        
        assertFalse(result.violated, "Should not flag when amount is below slab");
    }
}
