package com.fabrica.p6f5.springapp.pdf.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * PdfLog entity following Single Responsibility Principle.
 * Tracks PDF generation attempts and results.
 */
@Entity
@Table(name = "pdf_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pdf_log_id")
    private Long id;
    
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;
    
    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "generation_status", nullable = false, length = 50)
    private GenerationStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "template_type", length = 100)
    private String templateType;
    
    @Column(name = "generated_by")
    private Long generatedBy;
    
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
    
    /**
     * PDF generation status enum
     */
    public enum GenerationStatus {
        SUCCESS,
        FAILED,
        PENDING
    }
}

