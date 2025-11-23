package com.fabrica.p6f5.springapp.shipment.repository;

import com.fabrica.p6f5.springapp.shipment.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Shipment Repository interface following Dependency Inversion Principle.
 * Defines data access operations for shipments.
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    /**
     * Find a shipment by tracking number.
     * 
     * @param trackingNumber the tracking number
     * @return Optional containing the shipment if found
     */
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    /**
     * Find all shipments by client name.
     * 
     * @param clientName the client name
     * @return list of shipments for the client
     */
    List<Shipment> findByClientNameOrderByCreatedAtDesc(String clientName);
    
    /**
     * Find all shipments by status.
     * 
     * @param status the shipment status
     * @return list of shipments with the specified status
     */
    List<Shipment> findByStatusOrderByCreatedAtDesc(Shipment.ShipmentStatus status);
    
    /**
     * Check if a tracking number already exists.
     * 
     * @param trackingNumber the tracking number to check
     * @return true if exists, false otherwise
     */
    boolean existsByTrackingNumber(String trackingNumber);
    
    /**
     * Find shipments created by a specific user.
     * 
     * @param userId the user ID
     * @return list of shipments created by the user
     */
    List<Shipment> findByCreatedByOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find shipments not yet linked to any invoice.
     * 
     * @return list of unlinked shipments
     */
    @Query("SELECT s FROM Shipment s WHERE s.id NOT IN " +
           "(SELECT DISTINCT is.shipment.id FROM com.fabrica.p6f5.springapp.invoice.model.InvoiceShipment is)")
    List<Shipment> findUnlinkedShipments();
}

