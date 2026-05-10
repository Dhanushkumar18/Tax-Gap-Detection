package com.taxgap.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerExceptionSummaryDTO {
    private Long customerId;
    private Long exceptionCount;
}
