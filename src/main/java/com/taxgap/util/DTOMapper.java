package com.taxgap.util;

import com.taxgap.dto.TransactionResponseDTO;
import com.taxgap.dto.ExceptionResponseDTO;
import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxException;

public class DTOMapper {
    
    public static TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
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
    
    public static ExceptionResponseDTO mapToExceptionDTO(TaxException exception) {
        return ExceptionResponseDTO.builder()
            .id(exception.getId())
            .transactionId(exception.getTransactionId())
            .customerId(exception.getCustomerId())
            .ruleName(exception.getRuleName())
            .severity(exception.getSeverity().toString())
            .message(exception.getMessage())
            .timestamp(exception.getTimestamp())
            .build();
    }
}
