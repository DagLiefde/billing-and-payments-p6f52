package com.fabrica.p6f5.springapp.invoice.service;

import com.fabrica.p6f5.springapp.audit.model.AuditLog;
import com.fabrica.p6f5.springapp.audit.service.AuditService;
import com.fabrica.p6f5.springapp.exception.BusinessException;
import com.fabrica.p6f5.springapp.exception.ResourceNotFoundException;
import com.fabrica.p6f5.springapp.invoice.dto.CreateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.dto.InvoiceResponse;
import com.fabrica.p6f5.springapp.invoice.dto.UpdateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import com.fabrica.p6f5.springapp.invoice.model.InvoiceShipment;
import com.fabrica.p6f5.springapp.invoice.repository.InvoiceRepository;
import com.fabrica.p6f5.springapp.invoice.repository.InvoiceShipmentRepository;
import com.fabrica.p6f5.springapp.shipment.model.Shipment;
import com.fabrica.p6f5.springapp.shipment.repository.ShipmentRepository;
import com.fabrica.p6f5.springapp.util.Constants;
import com.fabrica.p6f5.springapp.util.InvoiceUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Invoice Service following Single Responsibility Principle.
 * Handles all invoice business logic.
 */
@Service
public class InvoiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    
    private final InvoiceRepository invoiceRepository;
    private final InvoiceShipmentRepository invoiceShipmentRepository;
    private final ShipmentRepository shipmentRepository;
    private final AuditService auditService;
    private final InvoiceItemService invoiceItemService;
    
    public InvoiceService(
            InvoiceRepository invoiceRepository,
            InvoiceShipmentRepository invoiceShipmentRepository,
            ShipmentRepository shipmentRepository,
            AuditService auditService,
            InvoiceItemService invoiceItemService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceShipmentRepository = invoiceShipmentRepository;
        this.shipmentRepository = shipmentRepository;
        this.auditService = auditService;
        this.invoiceItemService = invoiceItemService;
    }
    
    /**
     * Create a draft invoice
     */
    @Transactional
    public InvoiceResponse createDraftInvoice(CreateInvoiceRequest request, Long createdBy) {
        logger.info("Creating draft invoice for client: {}", request.getClientName());
        
        Invoice invoice = createInvoiceFromRequest(request, createdBy);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        invoiceItemService.addInvoiceItems(savedInvoice, request.getItems());
        linkShipments(savedInvoice, request.getShipmentIds());
        
        logAuditEvent(savedInvoice, createdBy, AuditLog.AuditAction.CREATE, Constants.AUDIT_CREATE_DRAFT);
        logger.info("Draft invoice created with id: {}", savedInvoice.getId());
        
        return getInvoiceResponse(savedInvoice.getId());
    }
    
    /**
     * Create invoice entity from request.
     */
    private Invoice createInvoiceFromRequest(CreateInvoiceRequest request, Long createdBy) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(InvoiceUtils.generateInvoiceNumber());
        invoice.setClientName(request.getClientName());
        invoice.setClientEmail(request.getClientEmail());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setCurrency(request.getCurrency() != null ? request.getCurrency() : Constants.DEFAULT_CURRENCY);
        invoice.setCreatedBy(createdBy);
        
        BigDecimal subtotal = InvoiceUtils.calculateSubtotal(request.getItems());
        BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(subtotal.add(taxAmount));
        
        return invoice;
    }
    
    
    /**
     * Link shipments to invoice.
     */
    private void linkShipments(Invoice invoice, List<Long> shipmentIds) {
        if (shipmentIds == null || shipmentIds.isEmpty()) {
            return;
        }
        
        List<InvoiceShipment> invoiceShipments = new ArrayList<>();
        for (Long shipmentId : shipmentIds) {
            validateShipmentNotLinked(shipmentId);
            InvoiceShipment invoiceShipment = createInvoiceShipment(invoice, shipmentId);
            invoiceShipments.add(invoiceShipment);
        }
        invoiceShipmentRepository.saveAll(invoiceShipments);
    }
    
    /**
     * Validate that shipment is not already linked.
     */
    private void validateShipmentNotLinked(Long shipmentId) {
        if (invoiceShipmentRepository.existsByShipmentId(shipmentId)) {
            throw new BusinessException(String.format(Constants.SHIPMENT_ALREADY_LINKED, shipmentId));
        }
    }
    
    /**
     * Create invoice-shipment relationship.
     */
    private InvoiceShipment createInvoiceShipment(Invoice invoice, Long shipmentId) {
        Shipment shipment = findShipmentById(shipmentId);
        InvoiceShipment invoiceShipment = new InvoiceShipment();
        invoiceShipment.setInvoice(invoice);
        invoiceShipment.setShipment(shipment);
        return invoiceShipment;
    }
    
    /**
     * Update a draft invoice
     */
    @Transactional
    public InvoiceResponse updateDraftInvoice(Long invoiceId, UpdateInvoiceRequest request, Long updatedBy) {
        logger.info("Updating draft invoice id: {}", invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        validateInvoiceCanBeEdited(invoice);
        validateVersion(invoice, request.getVersion());
        
        saveInvoiceHistory(invoice);
        Invoice oldInvoice = InvoiceUtils.copyInvoice(invoice);
        
        updateInvoiceFields(invoice, request);
        invoiceItemService.updateInvoiceItems(invoice, invoiceId, request.getItems());
        updateInvoiceShipments(invoice, invoiceId, request.getShipmentIds());
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logAuditEvent(updatedInvoice, updatedBy, AuditLog.AuditAction.UPDATE, Constants.AUDIT_UPDATE_DRAFT, oldInvoice);
        
        logger.info("Draft invoice updated with id: {}", updatedInvoice.getId());
        return getInvoiceResponse(updatedInvoice.getId());
    }
    
    /**
     * Find invoice by ID or throw exception.
     */
    private Invoice findInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.INVOICE_NOT_FOUND + invoiceId));
    }
    
    /**
     * Get invoice entity by ID (public method for services that need the entity).
     */
    public Invoice getInvoiceEntityById(Long invoiceId) {
        return findInvoiceById(invoiceId);
    }
    
    /**
     * Validate that invoice can be edited.
     */
    private void validateInvoiceCanBeEdited(Invoice invoice) {
        if (!invoice.canBeEdited()) {
            throw new BusinessException(String.format(Constants.INVOICE_CANNOT_BE_EDITED, invoice.getStatus()));
        }
    }
    
    /**
     * Validate version for optimistic concurrency control.
     */
    private void validateVersion(Invoice invoice, Integer requestVersion) {
        if (requestVersion != null && !requestVersion.equals(invoice.getVersion())) {
            throw new BusinessException(Constants.INVOICE_MODIFIED);
        }
    }
    
    /**
     * Save invoice history before update.
     */
    private void saveInvoiceHistory(Invoice invoice) {
        auditService.saveInvoiceHistory(
            invoice.getId(),
            invoice.getVersion(),
            invoice.getFiscalFolio(),
            invoice.getInvoiceNumber(),
            invoice,
            invoice.getCreatedBy()
        );
    }
    
    /**
     * Update invoice fields from request.
     */
    private void updateInvoiceFields(Invoice invoice, UpdateInvoiceRequest request) {
        invoice.setClientName(request.getClientName());
        invoice.setClientEmail(request.getClientEmail());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setCurrency(request.getCurrency() != null ? request.getCurrency() : Constants.DEFAULT_CURRENCY);
        invoice.setTaxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO);
        
        BigDecimal subtotal = InvoiceUtils.calculateSubtotal(request.getItems());
        invoice.setSubtotal(subtotal);
        invoice.setTotalAmount(subtotal.add(invoice.getTaxAmount()));
    }
    
    
    /**
     * Update invoice shipments.
     */
    private void updateInvoiceShipments(Invoice invoice, Long invoiceId, List<Long> shipmentIds) {
        invoiceShipmentRepository.deleteByInvoiceId(invoiceId);
        
        if (shipmentIds == null || shipmentIds.isEmpty()) {
            return;
        }
        
        List<InvoiceShipment> invoiceShipments = new ArrayList<>();
        for (Long shipmentId : shipmentIds) {
            validateShipmentForUpdate(invoiceId, shipmentId);
            InvoiceShipment invoiceShipment = createInvoiceShipment(invoice, shipmentId);
            invoiceShipments.add(invoiceShipment);
        }
        invoiceShipmentRepository.saveAll(invoiceShipments);
    }
    
    /**
     * Validate shipment for update (can be linked to same invoice but not another).
     */
    private void validateShipmentForUpdate(Long invoiceId, Long shipmentId) {
        if (!invoiceShipmentRepository.existsByShipmentId(shipmentId)) {
            return;
        }
        
        Optional<InvoiceShipment> existing = invoiceShipmentRepository.findByInvoiceIdAndShipmentId(invoiceId, shipmentId);
        if (existing.isEmpty()) {
            throw new BusinessException(String.format(Constants.SHIPMENT_LINKED_TO_ANOTHER, shipmentId));
        }
    }
    
    /**
     * Issue an invoice
     */
    @Transactional
    public InvoiceResponse issueInvoice(Long invoiceId, Long issuedBy) {
        logger.info("Issuing invoice id: {}", invoiceId);
        
        Invoice invoice = findInvoiceById(invoiceId);
        validateInvoiceCanBeIssued(invoice);
        
        ensureFiscalFolioExists(invoice);
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        
        Invoice issuedInvoice = invoiceRepository.save(invoice);
        saveInvoiceHistoryOnIssue(issuedInvoice, issuedBy);
        logAuditEvent(issuedInvoice, issuedBy, AuditLog.AuditAction.ISSUE, Constants.AUDIT_ISSUE, invoice);
        
        logger.info("Invoice issued with fiscal folio: {}", issuedInvoice.getFiscalFolio());
        return getInvoiceResponse(issuedInvoice.getId());
    }
    
    /**
     * Validate that invoice can be issued.
     */
    private void validateInvoiceCanBeIssued(Invoice invoice) {
        if (!invoice.canBeIssued()) {
            throw new BusinessException(Constants.INVOICE_CANNOT_BE_ISSUED);
        }
    }
    
    /**
     * Ensure fiscal folio exists, generate if not.
     */
    private void ensureFiscalFolioExists(Invoice invoice) {
        if (invoice.getFiscalFolio() == null) {
            invoice.setFiscalFolio(InvoiceUtils.generateFiscalFolio());
        }
    }
    
    /**
     * Save invoice history on issue.
     */
    private void saveInvoiceHistoryOnIssue(Invoice invoice, Long issuedBy) {
        auditService.saveInvoiceHistory(
            invoice.getId(),
            invoice.getVersion(),
            invoice.getFiscalFolio(),
            invoice.getInvoiceNumber(),
            invoice,
            issuedBy
        );
    }
    
    /**
     * Get invoice by ID
     */
    public InvoiceResponse getInvoiceById(Long invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);
        return InvoiceResponse.fromEntity(invoice);
    }
    
    /**
     * Get all invoices by status
     */
    public List<InvoiceResponse> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatusOrderByCreatedAtDesc(status).stream()
            .map(InvoiceResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all invoices
     */
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
            .map(InvoiceResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Find shipment by ID or throw exception.
     */
    private Shipment findShipmentById(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.SHIPMENT_NOT_FOUND + shipmentId));
    }
    
    /**
     * Get invoice response with fresh data from database.
     */
    private InvoiceResponse getInvoiceResponse(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.INVOICE_NOT_FOUND + invoiceId));
        return InvoiceResponse.fromEntity(invoice);
    }
    
    /**
     * Log audit event.
     */
    private void logAuditEvent(Invoice invoice, Long userId, AuditLog.AuditAction action, String summary) {
        logAuditEvent(invoice, userId, action, summary, null);
    }
    
    /**
     * Log audit event with old data.
     */
    private void logAuditEvent(Invoice invoice, Long userId, AuditLog.AuditAction action, String summary, Invoice oldInvoice) {
        auditService.logEvent(Constants.ENTITY_TYPE_INVOICE, invoice.getId(), action, userId, oldInvoice, invoice, summary);
    }
}

