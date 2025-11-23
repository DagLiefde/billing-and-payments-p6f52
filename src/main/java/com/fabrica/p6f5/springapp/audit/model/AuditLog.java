package com.fabrica.p6f5.springapp.audit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * AuditLog entity following Single Responsibility Principle.
 * Tracks all changes across the system for audit purposes.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long id;
    
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;
    
    @Column(name = "changed_by")
    private Long changedBy;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private String oldData;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;
    
    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Audit action enum
     */
    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE,
        ISSUE,
        REVERT,
        PUBLISH
    }
}

