package com.fabrica.p6f5.springapp.invoice.model;

import com.fabrica.p6f5.springapp.shipment.model.Shipment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * InvoiceShipment entity following Single Responsibility Principle.
 * Represents the many-to-many relationship between invoices and shipments.
 */
@Entity
@Table(name = "invoice_shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceShipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_shipment_id")
    private Long id;
    
    @NotNull(message = "Invoice is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @NotNull(message = "Shipment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

