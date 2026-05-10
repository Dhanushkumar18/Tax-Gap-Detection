package com.taxgap.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionSummaryReportDTO {
    private Long totalExceptions;
    private Long highSeverityCount;
    private Long mediumSeverityCount;
    private Long lowSeverityCount;
    private List<CustomerExceptionSummaryDTO> customerWiseSummary;
}
