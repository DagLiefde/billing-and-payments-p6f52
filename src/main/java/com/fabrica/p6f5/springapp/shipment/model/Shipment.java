package com.fabrica.p6f5.springapp.shipment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shipment entity following Single Responsibility Principle.
 * Represents a shipment that can be linked to invoices.
 */
@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;
    
    @NotBlank(message = "Client name is required")
    @Column(name = "client_name", nullable = false)
    private String clientName;
    
    @NotBlank(message = "Origin address is required")
    @Column(name = "origin_address", nullable = false, columnDefinition = "TEXT")
    private String originAddress;
    
    @NotBlank(message = "Destination address is required")
    @Column(name = "destination_address", nullable = false, columnDefinition = "TEXT")
    private String destinationAddress;
    
    @NotNull(message = "Total weight is required")
    @Positive(message = "Total weight must be positive")
    @Column(name = "total_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWeight;
    
    @NotNull(message = "Total volume is required")
    @Positive(message = "Total volume must be positive")
    @Column(name = "total_volume", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVolume;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false, length = 50)
    private ShipmentStatus status = ShipmentStatus.PENDING;
    
    @Column(name = "tracking_number", unique = true, length = 100)
    private String trackingNumber;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Shipment status enum
     */
    public enum ShipmentStatus {
        PENDING,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }
}

