# Shipment Service

## Overview
The Shipment Service manages shipment information and tracking.

## Features
- Track shipments with origin and destination
- Monitor shipment status
- Query unlinked shipments available for invoicing
- Link shipments to invoices

## API Endpoints

### Base URL
```
/api/v1/shipments
```

### Endpoints

#### Get All Shipments
```http
GET /api/v1/shipments
Authorization: Bearer {token}
```

#### Get Shipments by Status
```http
GET /api/v1/shipments/status/{status}
Authorization: Bearer {token}
```

Available statuses: `PENDING`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`

#### Get Unlinked Shipments
```http
GET /api/v1/shipments/unlinked
Authorization: Bearer {token}
```

Returns shipments not yet linked to any invoice.

## Shipment States
- **PENDING**: Awaiting processing
- **IN_TRANSIT**: Currently being shipped
- **DELIVERED**: Successfully delivered
- **CANCELLED**: Shipment cancelled

## Data Model
- Client name
- Origin and destination addresses
- Weight and volume
- Tracking number
- Status
- Creation timestamp

## Integration Points
- **Invoice Service**: Shipments can be linked to invoices
- **Audit Service**: All shipment changes are logged

