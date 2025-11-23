package com.fabrica.p6f5.springapp.audit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * InvoiceHistory entity following Single Responsibility Principle.
 * Maintains version history for invoices to enable revert functionality.
 */
@Entity
@Table(name = "invoice_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;
    
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "fiscal_folio", length = 100)
    private String fiscalFolio;
    
    @Column(name = "invoice_number", nullable = false, length = 100)
    private String invoiceNumber;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "invoice_data", nullable = false, columnDefinition = "jsonb")
    private String invoiceData;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_reverted")
    private Boolean isReverted = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

