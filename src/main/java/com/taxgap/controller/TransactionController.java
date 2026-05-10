package com.taxgap.controller;

import com.taxgap.dto.*;
import com.taxgap.service.TransactionProcessingService;
import com.taxgap.service.ReportingService;
import com.taxgap.repository.TransactionRepository;
import com.taxgap.repository.ExceptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    
    private final TransactionProcessingService transactionProcessingService;
    private final TransactionRepository transactionRepository;
    private final ReportingService reportingService;
    
    public TransactionController(TransactionProcessingService transactionProcessingService,
                               TransactionRepository transactionRepository,
                               ReportingService reportingService) {
        this.transactionProcessingService = transactionProcessingService;
        this.transactionRepository = transactionRepository;
        this.reportingService = reportingService;
    }
    
    @PostMapping("/upload-batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<BatchUploadResponseDTO>> uploadTransactionBatch(
            @RequestBody TransactionBatchRequestDTO batchRequest) {
        
        List<TransactionResponseDTO> responses = 
            transactionProcessingService.processTransactionBatch(batchRequest.getTransactions());
        
        long successCount = responses.stream()
            .filter(r -> r.getValidationStatus().equals("SUCCESS"))
            .count();
        long failureCount = responses.size() - successCount;
        
        BatchUploadResponseDTO responseData = BatchUploadResponseDTO.builder()
            .totalCount(responses.size())
            .successCount((int) successCount)
            .failureCount((int) failureCount)
            .transactions(responses)
            .build();
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Batch uploaded successfully", responseData));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
            .map(transaction -> {
                TransactionResponseDTO dto = com.taxgap.util.DTOMapper.mapToResponseDTO(transaction);
                return ResponseEntity.ok(new ApiResponseDTO<>(true, "Transaction retrieved", dto));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<List<TransactionResponseDTO>>> getCustomerTransactions(
            @PathVariable Long customerId) {
        
        List<TransactionResponseDTO> transactions = transactionRepository
            .findByCustomerId(customerId)
            .stream()
            .map(com.taxgap.util.DTOMapper::mapToResponseDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Transactions retrieved", transactions));
    }
}
