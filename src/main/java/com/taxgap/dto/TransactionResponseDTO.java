package com.taxgap.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Long id;
    private String transactionId;
    private LocalDate date;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private BigDecimal reportedTax;
    private String transactionType;
    private String validationStatus;
    private String failureReasons;
    private BigDecimal expectedTax;
    private BigDecimal taxGap;
    private String complianceStatus;
    private LocalDateTime createdAt;
}
