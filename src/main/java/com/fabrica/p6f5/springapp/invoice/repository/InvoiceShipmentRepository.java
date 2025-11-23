package com.fabrica.p6f5.springapp.invoice.repository;

import com.fabrica.p6f5.springapp.invoice.model.InvoiceShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Invoice Shipment Repository interface.
 * Defines data access operations for invoice-shipment relationships.
 */
@Repository
public interface InvoiceShipmentRepository extends JpaRepository<InvoiceShipment, Long> {
    
    /**
     * Find all shipments for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return list of invoice-shipment relationships
     */
    List<InvoiceShipment> findByInvoiceId(Long invoiceId);
    
    /**
     * Find an invoice-shipment relationship.
     * 
     * @param invoiceId the invoice ID
     * @param shipmentId the shipment ID
     * @return Optional containing the relationship if found
     */
    Optional<InvoiceShipment> findByInvoiceIdAndShipmentId(Long invoiceId, Long shipmentId);
    
    /**
     * Check if a shipment is already linked to an invoice.
     * 
     * @param shipmentId the shipment ID
     * @return true if linked, false otherwise
     */
    boolean existsByShipmentId(Long shipmentId);
    
    /**
     * Delete all relationships for an invoice.
     * 
     * @param invoiceId the invoice ID
     */
    void deleteByInvoiceId(Long invoiceId);
}

