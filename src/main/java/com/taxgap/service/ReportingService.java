package com.taxgap.service;

import com.taxgap.dto.CustomerTaxSummaryDTO;
import com.taxgap.dto.ExceptionSummaryReportDTO;
import com.taxgap.dto.CustomerExceptionSummaryDTO;
import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxException;
import com.taxgap.entity.ExceptionSeverity;
import com.taxgap.repository.TransactionRepository;
import com.taxgap.repository.ExceptionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    
    private final TransactionRepository transactionRepository;
    private final ExceptionRepository exceptionRepository;
    
    public ReportingService(TransactionRepository transactionRepository,
                           ExceptionRepository exceptionRepository) {
        this.transactionRepository = transactionRepository;
        this.exceptionRepository = exceptionRepository;
    }
    
    public CustomerTaxSummaryDTO getCustomerTaxSummary(Long customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalReportedTax = BigDecimal.ZERO;
        BigDecimal totalExpectedTax = BigDecimal.ZERO;
        BigDecimal totalTaxGap = BigDecimal.ZERO;
        long nonCompliantCount = 0;
        
        for (Transaction t : transactions) {
            totalAmount = totalAmount.add(t.getAmount());
            if (t.getReportedTax() != null) {
                totalReportedTax = totalReportedTax.add(t.getReportedTax());
            }
            if (t.getExpectedTax() != null) {
                totalExpectedTax = totalExpectedTax.add(t.getExpectedTax());
            }
            if (t.getTaxGap() != null) {
                totalTaxGap = totalTaxGap.add(t.getTaxGap());
            }
            
            if (t.getComplianceStatus() != null && 
                !t.getComplianceStatus().toString().equals("COMPLIANT")) {
                nonCompliantCount++;
            }
        }
        
        long totalTransactions = transactions.size();
        double complianceScore = 100.0;
        if (totalTransactions > 0) {
            complianceScore = 100.0 - ((double) nonCompliantCount / totalTransactions * 100);
        }
        
        return CustomerTaxSummaryDTO.builder()
            .customerId(customerId)
            .totalAmount(totalAmount)
            .totalReportedTax(totalReportedTax)
            .totalExpectedTax(totalExpectedTax)
            .totalTaxGap(totalTaxGap)
            .totalTransactions(totalTransactions)
            .nonCompliantTransactions(nonCompliantCount)
            .complianceScore(Math.round(complianceScore * 100.0) / 100.0)
            .build();
    }
    
    public ExceptionSummaryReportDTO getExceptionSummaryReport() {
        List<TaxException> exceptions = exceptionRepository.findAll();
        
        long highSeverityCount = exceptionRepository.countBySeverity(ExceptionSeverity.HIGH);
        long mediumSeverityCount = exceptionRepository.countBySeverity(ExceptionSeverity.MEDIUM);
        long lowSeverityCount = exceptionRepository.countBySeverity(ExceptionSeverity.LOW);
        
        // Group by customer
        Map<Long, Long> customerExceptionCounts = new HashMap<>();
        for (TaxException exception : exceptions) {
            customerExceptionCounts.merge(exception.getCustomerId(), 1L, Long::sum);
        }
        
        List<CustomerExceptionSummaryDTO> customerSummaries =
            customerExceptionCounts.entrySet().stream()
                .map(e -> new CustomerExceptionSummaryDTO(
                    e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        
        return ExceptionSummaryReportDTO.builder()
            .totalExceptions((long) exceptions.size())
            .highSeverityCount(highSeverityCount)
            .mediumSeverityCount(mediumSeverityCount)
            .lowSeverityCount(lowSeverityCount)
            .customerWiseSummary(customerSummaries)
            .build();
    }
}
