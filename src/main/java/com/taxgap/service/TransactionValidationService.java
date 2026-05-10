package com.taxgap.service;

import com.taxgap.dto.TransactionRequestDTO;
import com.taxgap.entity.Transaction;
import com.taxgap.entity.ValidationStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionValidationService {
    
    public ValidationResult validateTransaction(TransactionRequestDTO dto) {
        List<String> errors = new ArrayList<>();
        
        // Validate required fields
        if (dto.getTransactionId() == null || dto.getTransactionId().isBlank()) {
            errors.add("Transaction ID is required");
        }
        if (dto.getDate() == null) {
            errors.add("Date is required");
        }
        if (dto.getCustomerId() == null) {
            errors.add("Customer ID is required");
        }
        if (dto.getAmount() == null) {
            errors.add("Amount is required");
        }
        if (dto.getTaxRate() == null) {
            errors.add("Tax Rate is required");
        }
        if (dto.getTransactionType() == null || dto.getTransactionType().isBlank()) {
            errors.add("Transaction Type is required");
        }
        
        // Validate amount > 0
        if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Amount must be greater than 0");
        }
        
        // Validate date is not in future
        if (dto.getDate() != null && dto.getDate().isAfter(LocalDate.now())) {
            errors.add("Transaction date cannot be in the future");
        }
        
        // Validate transaction type
        if (dto.getTransactionType() != null) {
            try {
                com.taxgap.entity.TransactionType.valueOf(dto.getTransactionType());
            } catch (IllegalArgumentException e) {
                errors.add("Invalid transaction type. Must be SALE, REFUND, or EXPENSE");
            }
        }
        
        // Validate tax rate range (0 - 100%)
        if (dto.getTaxRate() != null) {
            if (dto.getTaxRate().compareTo(BigDecimal.ZERO) < 0 || 
                dto.getTaxRate().compareTo(new BigDecimal("1")) > 0) {
                errors.add("Tax rate must be between 0 and 1 (0-100%)");
            }
        }
        
        return new ValidationResult(!errors.isEmpty(), errors);
    }
    
    public static class ValidationResult {
        public final boolean hasErrors;
        public final List<String> errors;
        
        public ValidationResult(boolean hasErrors, List<String> errors) {
            this.hasErrors = hasErrors;
            this.errors = errors;
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}
