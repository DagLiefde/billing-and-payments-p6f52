package com.fabrica.p6f5.springapp.invoice.repository;

import com.fabrica.p6f5.springapp.invoice.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Invoice Item Repository interface.
 * Defines data access operations for invoice items.
 */
@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    /**
     * Find all items for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return list of items for the invoice
     */
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    
    /**
     * Delete all items for an invoice.
     * 
     * @param invoiceId the invoice ID
     */
    void deleteByInvoiceId(Long invoiceId);
}

