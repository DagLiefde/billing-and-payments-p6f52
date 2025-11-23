package com.fabrica.p6f5.springapp.util;

/**
 * Constants used throughout the application.
 * Eliminates magic numbers and strings.
 */
public final class Constants {
    
    private Constants() {
        // Utility class - prevent instantiation
    }
    
    // HTTP Header Constants
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = 7;
    
    // JWT Constants
    public static final int JWT_TOKEN_START_INDEX = 7;
    
    // Default Values
    public static final String DEFAULT_CURRENCY = "USD";
    
    // Error Messages
    public static final String INVOICE_NOT_FOUND = "Invoice not found with id: ";
    public static final String SHIPMENT_NOT_FOUND = "Shipment not found with id: ";
    public static final String SHIPMENT_ALREADY_LINKED = "Shipment %d is already linked to an invoice";
    public static final String SHIPMENT_LINKED_TO_ANOTHER = "Shipment %d is already linked to another invoice";
    public static final String INVOICE_CANNOT_BE_EDITED = "Invoice cannot be edited. Status: %s";
    public static final String INVOICE_MODIFIED = "Invoice has been modified by another user. Please refresh and try again.";
    public static final String INVOICE_CANNOT_BE_ISSUED = "Invoice cannot be issued. Missing required data or invalid status.";
    public static final String PDF_ONLY_ISSUED = "PDF can only be generated for ISSUED invoices. Current status: %s";
    public static final String PDF_GENERATION_FAILED = "Failed to generate PDF: %s";
    
    // Audit Messages
    public static final String AUDIT_CREATE_DRAFT = "Created draft invoice";
    public static final String AUDIT_UPDATE_DRAFT = "Updated draft invoice";
    public static final String AUDIT_ISSUE = "Issued invoice";
    
    // Entity Types
    public static final String ENTITY_TYPE_INVOICE = "Invoice";
}


