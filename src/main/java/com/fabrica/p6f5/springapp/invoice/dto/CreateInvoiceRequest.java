package com.fabrica.p6f5.springapp.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating an invoice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    
    @NotBlank(message = "Client name is required")
    private String clientName;
    
    private String clientEmail;
    
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<InvoiceItemRequest> items;
    
    private List<Long> shipmentIds;
    
    @NotNull(message = "Tax amount is required")
    private BigDecimal taxAmount;
    
    private String currency = "USD";
    
    /**
     * Nested DTO for invoice items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemRequest {
        
        private Long shipmentId;
        
        @NotBlank(message = "Description is required")
        private String description;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;
    }
}

