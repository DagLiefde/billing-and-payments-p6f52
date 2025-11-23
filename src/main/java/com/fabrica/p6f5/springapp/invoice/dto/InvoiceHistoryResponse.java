package com.fabrica.p6f5.springapp.invoice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for invoice history responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHistoryResponse {
    
    private Long id;
    private Long invoiceId;
    private Integer version;
    private String fiscalFolio;
    private String invoiceNumber;
    private String invoiceData;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Boolean isReverted;
}

