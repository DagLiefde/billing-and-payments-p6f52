package com.fabrica.p6f5.springapp.audit.repository;

import com.fabrica.p6f5.springapp.audit.model.InvoiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Invoice History Repository interface.
 * Defines data access operations for invoice history.
 */
@Repository
public interface InvoiceHistoryRepository extends JpaRepository<InvoiceHistory, Long> {
    
    /**
     * Find all versions for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return list of versions ordered by version number descending
     */
    List<InvoiceHistory> findByInvoiceIdOrderByVersionDesc(Long invoiceId);
    
    /**
     * Find a specific version of an invoice.
     * 
     * @param invoiceId the invoice ID
     * @param version the version number
     * @return Optional containing the invoice history if found
     */
    Optional<InvoiceHistory> findByInvoiceIdAndVersion(Long invoiceId, Integer version);
    
    /**
     * Find the latest version of an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return Optional containing the latest invoice history
     */
    Optional<InvoiceHistory> findFirstByInvoiceIdOrderByVersionDesc(Long invoiceId);
    
    /**
     * Count versions for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return count of versions for the invoice
     */
    long countByInvoiceId(Long invoiceId);
}

