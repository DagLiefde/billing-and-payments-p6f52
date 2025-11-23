package com.fabrica.p6f5.springapp.invoice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice entity following Single Responsibility Principle.
 * Represents a business invoice with fiscal information.
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;
    
    @Column(name = "fiscal_folio", unique = true, length = 100)
    private String fiscalFolio;
    
    @NotBlank(message = "Invoice number is required")
    @Column(name = "invoice_number", unique = true, nullable = false, length = 100)
    private String invoiceNumber;
    
    @NotBlank(message = "Client name is required")
    @Column(name = "client_name", nullable = false)
    private String clientName;
    
    @Column(name = "client_email", length = 255)
    private String clientEmail;
    
    @NotNull(message = "Invoice date is required")
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @NotNull(message = "Subtotal is required")
    @PositiveOrZero(message = "Subtotal must be positive or zero")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @PositiveOrZero(message = "Tax amount must be positive or zero")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be positive or zero")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "currency", length = 10)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false, length = 50)
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;
    
    @NotNull(message = "Created by is required")
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version")
    private Integer version = 1;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceShipment> shipments = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (invoiceDate == null) {
            invoiceDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Invoice status enum
     */
    public enum InvoiceStatus {
        DRAFT,
        ISSUED,
        PAID,
        CANCELLED
    }
    
    /**
     * Check if invoice can be edited
     */
    public boolean canBeEdited() {
        return status == InvoiceStatus.DRAFT;
    }
    
    /**
     * Check if invoice can be issued
     */
    public boolean canBeIssued() {
        return status == InvoiceStatus.DRAFT && 
               subtotal != null && subtotal.compareTo(BigDecimal.ZERO) > 0 &&
               items != null && !items.isEmpty() &&
               clientName != null && !clientName.trim().isEmpty();
    }
}

