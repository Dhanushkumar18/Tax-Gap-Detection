package com.taxgap.controller;

import com.taxgap.dto.ApiResponseDTO;
import com.taxgap.dto.CustomerTaxSummaryDTO;
import com.taxgap.dto.ExceptionSummaryReportDTO;
import com.taxgap.service.ReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {
    
    private final ReportingService reportingService;
    
    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }
    
    @GetMapping("/customer-tax-summary/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<CustomerTaxSummaryDTO>> getCustomerTaxSummary(
            @PathVariable Long customerId) {
        
        CustomerTaxSummaryDTO summary = reportingService.getCustomerTaxSummary(customerId);
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Customer tax summary retrieved", summary));
    }
    
    @GetMapping("/exception-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ApiResponseDTO<ExceptionSummaryReportDTO>> getExceptionSummary() {
        
        ExceptionSummaryReportDTO summary = reportingService.getExceptionSummaryReport();
        
        return ResponseEntity.ok(new ApiResponseDTO<>(true, 
            "Exception summary report retrieved", summary));
    }
}
