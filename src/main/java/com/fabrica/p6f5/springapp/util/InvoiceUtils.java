package com.fabrica.p6f5.springapp.util;

import com.fabrica.p6f5.springapp.invoice.dto.CreateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.dto.UpdateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import com.fabrica.p6f5.springapp.invoice.model.InvoiceItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for invoice-related operations.
 * Reduces cyclomatic complexity by extracting common operations.
 */
public final class InvoiceUtils {
    
    private InvoiceUtils() {
        // Utility class - prevent instantiation
    }
    
    private static final String INVOICE_PREFIX = "INV-";
    private static final String FISCAL_PREFIX = "FISCAL-";
    private static final int UUID_LENGTH = 8;
    private static final int FISCAL_UUID_LENGTH = 16;
    
    /**
     * Generate unique invoice number.
     */
    public static String generateInvoiceNumber() {
        String uuid = UUID.randomUUID().toString().substring(0, UUID_LENGTH).toUpperCase();
        return INVOICE_PREFIX + uuid + "-" + System.currentTimeMillis();
    }
    
    /**
     * Generate unique fiscal folio.
     */
    public static String generateFiscalFolio() {
        String uuid = UUID.randomUUID().toString().substring(0, FISCAL_UUID_LENGTH).toUpperCase();
        return FISCAL_PREFIX + uuid + "-" + System.currentTimeMillis();
    }
    
    /**
     * Calculate subtotal from items.
     */
    public static BigDecimal calculateSubtotal(List<?> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
            .map(InvoiceUtils::extractItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Extract item total from different request types.
     */
    private static BigDecimal extractItemTotal(Object item) {
        if (item instanceof CreateInvoiceRequest.InvoiceItemRequest req) {
            return calculateItemTotal(req.getUnitPrice(), req.getQuantity());
        } else if (item instanceof UpdateInvoiceRequest.InvoiceItemRequest req) {
            return calculateItemTotal(req.getUnitPrice(), req.getQuantity());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate total for a single item.
     */
    private static BigDecimal calculateItemTotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * Create invoice items from create request.
     */
    public static List<InvoiceItem> createInvoiceItems(
            List<CreateInvoiceRequest.InvoiceItemRequest> itemRequests, 
            Invoice invoice) {
        List<InvoiceItem> items = new ArrayList<>();
        for (CreateInvoiceRequest.InvoiceItemRequest itemRequest : itemRequests) {
            InvoiceItem item = createInvoiceItem(itemRequest, invoice);
            items.add(item);
        }
        return items;
    }
    
    /**
     * Create invoice item from request.
     */
    private static InvoiceItem createInvoiceItem(
            CreateInvoiceRequest.InvoiceItemRequest itemRequest, 
            Invoice invoice) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setDescription(itemRequest.getDescription());
        item.setQuantity(itemRequest.getQuantity());
        item.setUnitPrice(itemRequest.getUnitPrice());
        item.calculateTotal();
        return item;
    }
    
    /**
     * Create invoice items from update request.
     */
    public static List<InvoiceItem> createInvoiceItemsFromUpdate(
            List<UpdateInvoiceRequest.InvoiceItemRequest> itemRequests, 
            Invoice invoice) {
        List<InvoiceItem> items = new ArrayList<>();
        for (UpdateInvoiceRequest.InvoiceItemRequest itemRequest : itemRequests) {
            InvoiceItem item = createInvoiceItemFromUpdate(itemRequest, invoice);
            items.add(item);
        }
        return items;
    }
    
    /**
     * Create invoice item from update request.
     */
    private static InvoiceItem createInvoiceItemFromUpdate(
            UpdateInvoiceRequest.InvoiceItemRequest itemRequest, 
            Invoice invoice) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setDescription(itemRequest.getDescription());
        item.setQuantity(itemRequest.getQuantity());
        item.setUnitPrice(itemRequest.getUnitPrice());
        item.calculateTotal();
        return item;
    }
    
    /**
     * Copy invoice for history tracking.
     */
    public static Invoice copyInvoice(Invoice invoice) {
        Invoice copy = new Invoice();
        copy.setId(invoice.getId());
        copy.setFiscalFolio(invoice.getFiscalFolio());
        copy.setInvoiceNumber(invoice.getInvoiceNumber());
        copy.setClientName(invoice.getClientName());
        copy.setInvoiceDate(invoice.getInvoiceDate());
        copy.setDueDate(invoice.getDueDate());
        copy.setSubtotal(invoice.getSubtotal());
        copy.setTaxAmount(invoice.getTaxAmount());
        copy.setTotalAmount(invoice.getTotalAmount());
        copy.setCurrency(invoice.getCurrency());
        copy.setStatus(invoice.getStatus());
        copy.setCreatedBy(invoice.getCreatedBy());
        copy.setCreatedAt(invoice.getCreatedAt());
        copy.setUpdatedAt(invoice.getUpdatedAt());
        copy.setVersion(invoice.getVersion());
        return copy;
    }
}


