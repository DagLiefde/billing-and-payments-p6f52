package com.fabrica.p6f5.springapp.invoice.dto;

import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for invoice responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    
    private Long id;
    private String fiscalFolio;
    private String invoiceNumber;
    private String clientName;
    private String clientEmail;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private String pdfUrl;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
    private List<InvoiceItemResponse> items;
    private List<Long> shipmentIds;
    
    /**
     * Convert Invoice entity to InvoiceResponse DTO
     */
    public static InvoiceResponse fromEntity(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setFiscalFolio(invoice.getFiscalFolio());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setClientName(invoice.getClientName());
        response.setClientEmail(invoice.getClientEmail());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setDueDate(invoice.getDueDate());
        response.setSubtotal(invoice.getSubtotal());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setCurrency(invoice.getCurrency());
        response.setStatus(invoice.getStatus().toString());
        response.setPdfUrl(invoice.getPdfUrl());
        response.setCreatedBy(invoice.getCreatedBy());
        response.setCreatedAt(invoice.getCreatedAt());
        response.setUpdatedAt(invoice.getUpdatedAt());
        response.setVersion(invoice.getVersion());
        
        if (invoice.getItems() != null) {
            response.setItems(invoice.getItems().stream()
                .map(InvoiceItemResponse::fromEntity)
                .collect(Collectors.toList()));
        }
        
        if (invoice.getShipments() != null) {
            response.setShipmentIds(invoice.getShipments().stream()
                .map(is -> is.getShipment().getId())
                .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    /**
     * Nested DTO for invoice items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemResponse {
        
        private Long id;
        private Long shipmentId;
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        
        /**
         * Convert InvoiceItem entity to InvoiceItemResponse DTO
         */
        public static InvoiceItemResponse fromEntity(com.fabrica.p6f5.springapp.invoice.model.InvoiceItem item) {
            InvoiceItemResponse response = new InvoiceItemResponse();
            response.setId(item.getId());
            response.setDescription(item.getDescription());
            response.setQuantity(item.getQuantity());
            response.setUnitPrice(item.getUnitPrice());
            response.setTotalPrice(item.getTotalPrice());
            if (item.getShipment() != null) {
                response.setShipmentId(item.getShipment().getId());
            }
            return response;
        }
    }
}

