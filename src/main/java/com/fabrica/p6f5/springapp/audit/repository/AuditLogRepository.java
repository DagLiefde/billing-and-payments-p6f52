package com.fabrica.p6f5.springapp.audit.repository;

import com.fabrica.p6f5.springapp.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Audit Log Repository interface.
 * Defines data access operations for audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Find all audit logs for an entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of audit logs for the entity
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    
    /**
     * Find all audit logs by action.
     * 
     * @param action the audit action
     * @return list of audit logs with the specified action
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditLog.AuditAction action);
    
    /**
     * Find all audit logs by user.
     * 
     * @param userId the user ID
     * @return list of audit logs by the user
     */
    List<AuditLog> findByChangedByOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find audit logs within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of audit logs within the date range
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
}

