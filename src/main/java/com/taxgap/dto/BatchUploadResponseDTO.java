package com.taxgap.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchUploadResponseDTO {
    private Integer totalCount;
    private Integer successCount;
    private Integer failureCount;
    private List<TransactionResponseDTO> transactions;
}
