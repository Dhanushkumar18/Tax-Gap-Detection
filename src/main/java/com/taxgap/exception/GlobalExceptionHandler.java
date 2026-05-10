package com.taxgap.exception;

import com.taxgap.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ApiResponseDTO<>(false, ex.getMessage(), null),
            HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleInvalidTransactionException(
            InvalidTransactionException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ApiResponseDTO<>(false, ex.getMessage(), null),
            HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ApiResponseDTO<>(false, "Access Denied: " + ex.getMessage(), null),
            HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
            new ApiResponseDTO<>(false, "An error occurred: " + ex.getMessage(), null),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
