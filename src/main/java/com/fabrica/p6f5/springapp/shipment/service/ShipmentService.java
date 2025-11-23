package com.fabrica.p6f5.springapp.shipment.service;

import com.fabrica.p6f5.springapp.shipment.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

/**
 * Shipment Service following Single Responsibility Principle.
 * Handles all shipment business logic.
 */
@Service
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }
    
    // Business logic methods can be added here as needed
}

