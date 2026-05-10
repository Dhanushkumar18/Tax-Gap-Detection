package com.taxgap.service;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.ComplianceStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {
    
    private static final BigDecimal THRESHOLD = new BigDecimal("1");
    
    public TaxCalculationResult calculateTax(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        BigDecimal taxRate = transaction.getTaxRate();
        BigDecimal reportedTax = transaction.getReportedTax();
        
        // Calculate expected tax
        BigDecimal expectedTax = amount.multiply(taxRate)
            .setScale(2, RoundingMode.HALF_UP);
        
        // Calculate tax gap
        BigDecimal taxGap;
        ComplianceStatus complianceStatus;
        
        if (reportedTax == null) {
            taxGap = null;
            complianceStatus = ComplianceStatus.NON_COMPLIANT;
        } else {
            taxGap = expectedTax.subtract(reportedTax)
                .setScale(2, RoundingMode.HALF_UP);
            
            // Determine compliance status
            if (taxGap.abs().compareTo(THRESHOLD) <= 0) {
                complianceStatus = ComplianceStatus.COMPLIANT;
            } else if (taxGap.compareTo(BigDecimal.ZERO) > 0) {
                complianceStatus = ComplianceStatus.UNDERPAID;
            } else {
                complianceStatus = ComplianceStatus.OVERPAID;
            }
        }
        
        return new TaxCalculationResult(expectedTax, taxGap, complianceStatus);
    }
    
    public static class TaxCalculationResult {
        public final BigDecimal expectedTax;
        public final BigDecimal taxGap;
        public final ComplianceStatus complianceStatus;
        
        public TaxCalculationResult(BigDecimal expectedTax, BigDecimal taxGap, 
                                   ComplianceStatus complianceStatus) {
            this.expectedTax = expectedTax;
            this.taxGap = taxGap;
            this.complianceStatus = complianceStatus;
        }
    }
}
