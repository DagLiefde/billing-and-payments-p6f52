package com.fabrica.p6f5.springapp.invoice.controller;

import com.fabrica.p6f5.springapp.audit.model.InvoiceHistory;
import com.fabrica.p6f5.springapp.audit.service.AuditService;
import com.fabrica.p6f5.springapp.dto.ApiResponse;
import com.fabrica.p6f5.springapp.invoice.dto.InvoiceHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fabrica.p6f5.springapp.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Invoice History Controller following Single Responsibility Principle.
 * Handles all invoice history-related HTTP requests.
 */
@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoice History API", description = "API for managing invoice history")
public class InvoiceHistoryController {
    
    private final AuditService auditService;
    
    public InvoiceHistoryController(AuditService auditService) {
        this.auditService = auditService;
    }
    
    /**
     * Get invoice history
     */
    @GetMapping("/{invoiceId}/history")
    @Operation(summary = "Get invoice history", description = "Retrieves all versions of an invoice")
    public ResponseEntity<ApiResponse<List<InvoiceHistoryResponse>>> getInvoiceHistory(
            @Parameter(description = "Invoice ID") @PathVariable Long invoiceId) {
        List<InvoiceHistory> history = auditService.getInvoiceHistory(invoiceId);
        List<InvoiceHistoryResponse> response = history.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseUtils.success(response, "Invoice history retrieved successfully");
    }
    
    /**
     * Get specific version of invoice
     */
    @GetMapping("/{invoiceId}/history/{version}")
    @Operation(summary = "Get invoice version", description = "Retrieves a specific version of an invoice")
    public ResponseEntity<ApiResponse<InvoiceHistoryResponse>> getInvoiceVersion(
            @Parameter(description = "Invoice ID") @PathVariable Long invoiceId,
            @Parameter(description = "Version number") @PathVariable Integer version) {
        Optional<InvoiceHistory> history = auditService.getInvoiceHistoryVersion(invoiceId, version);
        if (history.isEmpty()) {
            return ResponseUtils.errorTyped("Version not found", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        InvoiceHistoryResponse response = convertToResponse(history.get());
        return ResponseUtils.success(response, "Invoice version retrieved successfully");
    }
    
    /**
     * Convert InvoiceHistory to response DTO
     */
    private InvoiceHistoryResponse convertToResponse(InvoiceHistory history) {
        InvoiceHistoryResponse response = new InvoiceHistoryResponse();
        response.setId(history.getId());
        response.setInvoiceId(history.getInvoiceId());
        response.setVersion(history.getVersion());
        response.setFiscalFolio(history.getFiscalFolio());
        response.setInvoiceNumber(history.getInvoiceNumber());
        response.setInvoiceData(history.getInvoiceData());
        response.setCreatedBy(history.getCreatedBy());
        response.setCreatedAt(history.getCreatedAt());
        response.setIsReverted(history.getIsReverted());
        return response;
    }
}

