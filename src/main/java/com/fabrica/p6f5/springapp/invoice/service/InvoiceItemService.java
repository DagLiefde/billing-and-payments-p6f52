package com.fabrica.p6f5.springapp.invoice.service;

import com.fabrica.p6f5.springapp.exception.ResourceNotFoundException;
import com.fabrica.p6f5.springapp.invoice.dto.CreateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.dto.UpdateInvoiceRequest;
import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import com.fabrica.p6f5.springapp.invoice.model.InvoiceItem;
import com.fabrica.p6f5.springapp.invoice.repository.InvoiceItemRepository;
import com.fabrica.p6f5.springapp.shipment.model.Shipment;
import com.fabrica.p6f5.springapp.shipment.repository.ShipmentRepository;
import com.fabrica.p6f5.springapp.util.Constants;
import com.fabrica.p6f5.springapp.util.InvoiceUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling invoice item operations.
 * Reduces cyclomatic complexity in InvoiceService.
 */
@Service
public class InvoiceItemService {
    
    private final InvoiceItemRepository invoiceItemRepository;
    private final ShipmentRepository shipmentRepository;
    
    public InvoiceItemService(
            InvoiceItemRepository invoiceItemRepository,
            ShipmentRepository shipmentRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.shipmentRepository = shipmentRepository;
    }
    
    /**
     * Add invoice items from create request.
     */
    public void addInvoiceItems(Invoice invoice, List<CreateInvoiceRequest.InvoiceItemRequest> itemRequests) {
        List<InvoiceItem> items = InvoiceUtils.createInvoiceItems(itemRequests, invoice);
        linkItemsToShipments(items, itemRequests);
        invoiceItemRepository.saveAll(items);
    }
    
    /**
     * Update invoice items from update request.
     */
    public void updateInvoiceItems(Invoice invoice, Long invoiceId, List<UpdateInvoiceRequest.InvoiceItemRequest> itemRequests) {
        invoiceItemRepository.deleteByInvoiceId(invoiceId);
        List<InvoiceItem> items = InvoiceUtils.createInvoiceItemsFromUpdate(itemRequests, invoice);
        linkItemsToShipmentsFromUpdate(items, itemRequests);
        invoiceItemRepository.saveAll(items);
    }
    
    /**
     * Link items to shipments from create request.
     */
    private void linkItemsToShipments(
            List<InvoiceItem> items, 
            List<CreateInvoiceRequest.InvoiceItemRequest> itemRequests) {
        for (int i = 0; i < items.size(); i++) {
            InvoiceItem item = items.get(i);
            CreateInvoiceRequest.InvoiceItemRequest itemRequest = itemRequests.get(i);
            linkItemToShipment(item, itemRequest.getShipmentId());
        }
    }
    
    /**
     * Link items to shipments from update request.
     */
    private void linkItemsToShipmentsFromUpdate(
            List<InvoiceItem> items, 
            List<UpdateInvoiceRequest.InvoiceItemRequest> itemRequests) {
        for (int i = 0; i < items.size(); i++) {
            InvoiceItem item = items.get(i);
            UpdateInvoiceRequest.InvoiceItemRequest itemRequest = itemRequests.get(i);
            linkItemToShipment(item, itemRequest.getShipmentId());
        }
    }
    
    /**
     * Link item to shipment if shipment ID is provided.
     */
    private void linkItemToShipment(InvoiceItem item, Long shipmentId) {
        if (shipmentId == null) {
            return;
        }
        Shipment shipment = findShipmentById(shipmentId);
        item.setShipment(shipment);
    }
    
    /**
     * Find shipment by ID or throw exception.
     */
    private Shipment findShipmentById(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.SHIPMENT_NOT_FOUND + shipmentId));
    }
}

