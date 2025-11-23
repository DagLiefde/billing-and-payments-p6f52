# Invoice Service

## Overview
The Invoice Service is responsible for managing the complete invoice lifecycle from creation to issuance and PDF generation.

## Features
- **Create Draft Invoice**: Create invoices in DRAFT status linking multiple shipments
- **Edit Draft Invoice**: Update invoices with optimistic concurrency control
- **Issue Invoice**: Transition invoices from DRAFT to ISSUED with fiscal folio generation
- **Generate PDF**: Create PDF documents for issued invoices
- **Version History**: Track all changes to invoices with full audit trail
- **Shipment Integration**: Link multiple shipments to a single invoice

## API Endpoints

### Base URL
```
/api/v1/invoices
```

### Endpoints

#### Create Draft Invoice
```http
POST /api/v1/invoices
Authorization: Bearer {token}
Content-Type: application/json

{
  "clientName": "Acme Corporation",
  "invoiceDate": "2024-01-15",
  "dueDate": "2024-02-15",
  "items": [
    {
      "description": "Shipping service",
      "quantity": 5,
      "unitPrice": 100.00,
      "shipmentId": 1
    }
  ],
  "shipmentIds": [1, 2, 3],
  "taxAmount": 100.00,
  "currency": "USD"
}
```

**Response:** 201 Created
```json
{
  "success": true,
  "message": "Draft invoice created successfully",
  "data": {
    "id": 1,
    "invoiceNumber": "INV-ABC12345-1234567890",
    "clientName": "Acme Corporation",
    "status": "DRAFT",
    "subtotal": 500.00,
    "taxAmount": 100.00,
    "totalAmount": 600.00,
    "items": [...]
  }
}
```

#### Update Draft Invoice
```http
PUT /api/v1/invoices/{invoiceId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "clientName": "Acme Corporation",
  "invoiceDate": "2024-01-15",
  "dueDate": "2024-02-15",
  "items": [...],
  "shipmentIds": [1, 2, 3],
  "taxAmount": 120.00,
  "version": 1
}
```

**Response:** 200 OK

#### Issue Invoice
```http
POST /api/v1/invoices/{invoiceId}/issue
Authorization: Bearer {token}
```

**Response:** 200 OK
```json
{
  "success": true,
  "message": "Invoice issued successfully",
  "data": {
    "id": 1,
    "fiscalFolio": "FISCAL-XYZ12345-1234567890",
    "status": "ISSUED",
    ...
  }
}
```

#### Generate PDF
```http
POST /api/v1/invoices/{invoiceId}/pdf
Authorization: Bearer {token}
```

**Response:** 200 OK
```json
{
  "success": true,
  "message": "PDF generated successfully",
  "data": "https://example.com/pdfs/invoice-1.pdf"
}
```

#### Get Invoice History
```http
GET /api/v1/invoices/{invoiceId}/history
Authorization: Bearer {token}
```

**Response:** 200 OK
```json
{
  "success": true,
  "message": "Invoice history retrieved successfully",
  "data": [
    {
      "version": 2,
      "createdAt": "2024-01-15T10:30:00",
      "invoiceData": "{...}",
      ...
    }
  ]
}
```

## Business Rules

### Invoice States
- **DRAFT**: Initial state, can be edited
- **ISSUED**: Finalized state, locked from edits
- **PAID**: Payment received
- **CANCELLED**: Invoice cancelled

### Validation Rules
- At least one item is required
- All items must have positive quantities and prices
- Subtotal must be positive or zero
- Tax amount must be positive or zero
- Total amount = subtotal + tax amount
- Cannot issue invoice without fiscal folio
- Cannot edit issued invoice

### Concurrency Control
- Optimistic locking via version field
- Conflicts detected when version mismatch occurs
- Full history maintained for audit and revert

## Integration Points
- **Audit Service**: Logs all actions and maintains version history
- **PDF Service**: Generates invoice documents
- **Shipment Service**: Links shipments to invoices

