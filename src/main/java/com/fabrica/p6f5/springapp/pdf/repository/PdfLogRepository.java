package com.fabrica.p6f5.springapp.pdf.repository;

import com.fabrica.p6f5.springapp.pdf.model.PdfLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PDF Log Repository interface.
 * Defines data access operations for PDF logs.
 */
@Repository
public interface PdfLogRepository extends JpaRepository<PdfLog, Long> {
    
    /**
     * Find all PDF logs for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return list of PDF logs for the invoice
     */
    List<PdfLog> findByInvoiceIdOrderByGeneratedAtDesc(Long invoiceId);
    
    /**
     * Find the latest PDF log for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return Optional containing the latest PDF log
     */
    Optional<PdfLog> findFirstByInvoiceIdOrderByGeneratedAtDesc(Long invoiceId);
    
    /**
     * Find all PDF logs by status.
     * 
     * @param status the generation status
     * @return list of PDF logs with the specified status
     */
    List<PdfLog> findByStatusOrderByGeneratedAtDesc(PdfLog.GenerationStatus status);
    
    /**
     * Find all successful PDF generations for an invoice.
     * 
     * @param invoiceId the invoice ID
     * @return list of successful PDF logs
     */
    List<PdfLog> findByInvoiceIdAndStatusOrderByGeneratedAtDesc(Long invoiceId, PdfLog.GenerationStatus status);
}

