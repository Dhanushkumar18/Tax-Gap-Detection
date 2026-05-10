package com.taxgap.controller;

import com.taxgap.dto.ApiResponseDTO;
import com.taxgap.dto.ExceptionResponseDTO;
import com.taxgap.entity.ExceptionSeverity;
import com.taxgap.repository.ExceptionRepository;
import com.taxgap.util.DTOMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/exceptions")
public class ExceptionController {
    
    private final ExceptionRepository exceptionRepository;
    
    public ExceptionController(ExceptionRepository exceptionRepository) {
        this.exceptionRepository = exceptionRepository;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<List<ExceptionResponseDTO>>> getAllExceptions() {
        List<ExceptionResponseDTO> exceptions = exceptionRepository.findAll()
            .stream()
            .map(DTOMapper::mapToExceptionDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Exceptions retrieved", exceptions));
    }
    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<List<ExceptionResponseDTO>>> getExceptionsByCustomer(
            @PathVariable Long customerId) {
        
        List<ExceptionResponseDTO> exceptions = exceptionRepository
            .findByCustomerId(customerId)
            .stream()
            .map(DTOMapper::mapToExceptionDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Customer exceptions retrieved", exceptions));
    }
    
    @GetMapping("/severity/{severity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<List<ExceptionResponseDTO>>> getExceptionsBySeverity(
            @PathVariable String severity) {
        
        try {
            ExceptionSeverity sev = ExceptionSeverity.valueOf(severity);
            List<ExceptionResponseDTO> exceptions = exceptionRepository
                .findBySeverity(sev)
                .stream()
                .map(DTOMapper::mapToExceptionDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new ApiResponseDTO<>(true, 
                "Exceptions by severity retrieved", exceptions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponseDTO<>(false, "Invalid severity level", null));
        }
    }
    
    @GetMapping("/rule/{ruleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<List<ExceptionResponseDTO>>> getExceptionsByRule(
            @PathVariable String ruleName) {
        
        List<ExceptionResponseDTO> exceptions = exceptionRepository
            .findByRuleName(ruleName)
            .stream()
            .map(DTOMapper::mapToExceptionDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Exceptions by rule retrieved", exceptions));
    }
}
