package com.fabrica.p6f5.springapp.exception;

import com.fabrica.p6f5.springapp.dto.ApiResponse;
import com.fabrica.p6f5.springapp.util.ResponseUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler following Single Responsibility Principle.
 * Centralizes all exception handling logic.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return ResponseUtils.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        logger.error("Business exception: {}", ex.getMessage());
        return ResponseUtils.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String errors = extractValidationErrors(ex);
        return ResponseUtils.error("Validation failed: " + errors, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Extract validation errors from exception.
     */
    private String extractValidationErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    }
    
    /**
     * Handle ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        logger.error("Constraint violation: {}", ex.getMessage());
        return ResponseUtils.error("Constraint violation: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseUtils.error("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

