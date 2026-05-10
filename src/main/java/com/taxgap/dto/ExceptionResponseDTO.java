package com.taxgap.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponseDTO {
    private Long id;
    private String transactionId;
    private Long customerId;
    private String ruleName;
    private String severity;
    private String message;
    private LocalDateTime timestamp;
}
