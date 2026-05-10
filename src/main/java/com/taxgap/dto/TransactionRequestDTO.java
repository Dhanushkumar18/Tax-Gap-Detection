package com.taxgap.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {
    private String transactionId;
    private LocalDate date;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private BigDecimal reportedTax;
    private String transactionType; // SALE, REFUND, EXPENSE
}
