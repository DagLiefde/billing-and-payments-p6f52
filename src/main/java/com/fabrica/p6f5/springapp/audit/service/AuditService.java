package com.fabrica.p6f5.springapp.audit.service;

import com.fabrica.p6f5.springapp.audit.model.AuditLog;
import com.fabrica.p6f5.springapp.audit.model.InvoiceHistory;
import com.fabrica.p6f5.springapp.audit.repository.AuditLogRepository;
import com.fabrica.p6f5.springapp.audit.repository.InvoiceHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Audit Service following Single Responsibility Principle.
 * Handles all audit logging and history tracking.
 */
@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    private final InvoiceHistoryRepository invoiceHistoryRepository;
    private final ObjectMapper objectMapper;
    
    public AuditService(AuditLogRepository auditLogRepository, 
                        InvoiceHistoryRepository invoiceHistoryRepository,
                        ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.invoiceHistoryRepository = invoiceHistoryRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Log an audit event
     */
    @Transactional
    public AuditLog logEvent(String entityType, Long entityId, AuditLog.AuditAction action, 
                            Long changedBy, Object oldData, Object newData, String changeSummary) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            auditLog.setChangedBy(changedBy);
            auditLog.setChangeSummary(changeSummary);
            
            if (oldData != null) {
                auditLog.setOldData(objectMapper.writeValueAsString(oldData));
            }
            if (newData != null) {
                auditLog.setNewData(objectMapper.writeValueAsString(newData));
            }
            
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            logger.error("Error logging audit event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to log audit event", e);
        }
    }
    
    /**
     * Save invoice history version
     */
    @Transactional
    public InvoiceHistory saveInvoiceHistory(Long invoiceId, Integer version, String fiscalFolio,
                                             String invoiceNumber, Object invoiceData, Long createdBy) {
        try {
            InvoiceHistory history = new InvoiceHistory();
            history.setInvoiceId(invoiceId);
            history.setVersion(version);
            history.setFiscalFolio(fiscalFolio);
            history.setInvoiceNumber(invoiceNumber);
            history.setInvoiceData(objectMapper.writeValueAsString(invoiceData));
            history.setCreatedBy(createdBy);
            
            return invoiceHistoryRepository.save(history);
        } catch (Exception e) {
            logger.error("Error saving invoice history: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save invoice history", e);
        }
    }
    
    /**
     * Get audit logs for an entity
     */
    public List<AuditLog> getAuditLogs(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
    
    /**
     * Get invoice history
     */
    public List<InvoiceHistory> getInvoiceHistory(Long invoiceId) {
        return invoiceHistoryRepository.findByInvoiceIdOrderByVersionDesc(invoiceId);
    }
    
    /**
     * Get specific version of invoice history
     */
    public Optional<InvoiceHistory> getInvoiceHistoryVersion(Long invoiceId, Integer version) {
        return invoiceHistoryRepository.findByInvoiceIdAndVersion(invoiceId, version);
    }
    
    /**
     * Get latest version of invoice
     */
    public Optional<InvoiceHistory> getLatestInvoiceVersion(Long invoiceId) {
        return invoiceHistoryRepository.findFirstByInvoiceIdOrderByVersionDesc(invoiceId);
    }
    
    /**
     * Get version count for an invoice
     */
    public long getInvoiceVersionCount(Long invoiceId) {
        return invoiceHistoryRepository.countByInvoiceId(invoiceId);
    }
}

