package com.taxgap.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionBatchRequestDTO {
    private List<TransactionRequestDTO> transactions;
}
