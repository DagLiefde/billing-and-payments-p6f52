package com.fabrica.p6f5.springapp.invoice.repository;

import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Invoice Repository interface following Dependency Inversion Principle.
 * Defines data access operations for invoices.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * Find an invoice by fiscal folio.
     * 
     * @param fiscalFolio the fiscal folio
     * @return Optional containing the invoice if found
     */
    Optional<Invoice> findByFiscalFolio(String fiscalFolio);
    
    /**
     * Find an invoice by invoice number.
     * 
     * @param invoiceNumber the invoice number
     * @return Optional containing the invoice if found
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    /**
     * Find all invoices by status.
     * 
     * @param status the invoice status
     * @return list of invoices with the specified status
     */
    List<Invoice> findByStatusOrderByCreatedAtDesc(Invoice.InvoiceStatus status);
    
    /**
     * Find all invoices by client name.
     * 
     * @param clientName the client name
     * @return list of invoices for the client
     */
    List<Invoice> findByClientNameOrderByInvoiceDateDesc(String clientName);
    
    /**
     * Find all invoices created by a specific user.
     * 
     * @param userId the user ID
     * @return list of invoices created by the user
     */
    List<Invoice> findByCreatedByOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all invoices within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of invoices within the date range
     */
    List<Invoice> findByInvoiceDateBetweenOrderByInvoiceDateDesc(LocalDate startDate, LocalDate endDate);
    
    /**
     * Check if a fiscal folio already exists.
     * 
     * @param fiscalFolio the fiscal folio to check
     * @return true if exists, false otherwise
     */
    boolean existsByFiscalFolio(String fiscalFolio);
    
    /**
     * Check if an invoice number already exists.
     * 
     * @param invoiceNumber the invoice number to check
     * @return true if exists, false otherwise
     */
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    /**
     * Find all drafts that can be issued.
     * 
     * @return list of draft invoices that can be issued
     */
    @Query("SELECT i FROM Invoice i WHERE i.status = 'DRAFT' AND " +
           "i.subtotal > 0 AND SIZE(i.items) > 0 AND i.clientName IS NOT NULL")
    List<Invoice> findDraftsReadyForIssuance();
    
    /**
     * Count invoices by status.
     * 
     * @param status the invoice status
     * @return count of invoices with the specified status
     */
    long countByStatus(Invoice.InvoiceStatus status);
}

