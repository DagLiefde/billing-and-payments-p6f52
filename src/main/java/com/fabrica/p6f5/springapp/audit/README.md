# Audit Service

## Overview
The Audit Service provides comprehensive audit logging and version history tracking for all entities.

## Features
- **Audit Logging**: Track all create, update, delete, issue operations
- **Version History**: Maintain full version history for invoices
- **Change Tracking**: Log old and new values for all changes
- **User Attribution**: Track who made what changes and when
- **Query Interface**: Search and retrieve audit logs and history

## Components

### AuditLog
Tracks all actions across the system:
- Entity type and ID
- Action type (CREATE, UPDATE, DELETE, ISSUE, REVERT, PUBLISH)
- User who performed the action
- Old and new data (JSON)
- Change summary
- Timestamp

### InvoiceHistory
Maintains version history for invoices:
- Invoice ID and version number
- Fiscal folio and invoice number
- Complete invoice data snapshot
- Creator information
- Revert status
- Timestamp

## Use Cases

### Audit Trail
Every action in the system is logged:
- Who performed the action
- When it occurred
- What changed
- Why it changed (via summary)

### Version Control
For invoices specifically:
- Complete history of all versions
- Ability to view any previous version
- Revert capability
- Conflict detection

## Integration Points
- **Invoice Service**: Automatically logs all invoice operations
- **PDF Service**: Tracks PDF generation attempts
- **Shipment Service**: Logs shipment status changes

