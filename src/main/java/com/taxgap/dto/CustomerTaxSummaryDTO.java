package com.taxgap.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerTaxSummaryDTO {
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalReportedTax;
    private BigDecimal totalExpectedTax;
    private BigDecimal totalTaxGap;
    private Long totalTransactions;
    private Long nonCompliantTransactions;
    private Double complianceScore;
}
