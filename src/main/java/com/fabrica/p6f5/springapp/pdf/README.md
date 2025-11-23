# PDF Service

## Overview
The PDF Service handles generation of PDF documents for invoices.

## Features
- Generate PDFs from invoice data
- Apply institutional templates
- Track generation status and errors
- Store PDF URLs in invoices

## API Endpoints

### Generate Invoice PDF
```http
POST /api/v1/invoices/{invoiceId}/pdf
Authorization: Bearer {token}
```

## Business Rules
- PDFs can only be generated for ISSUED invoices
- All generation attempts are logged
- Success/failure status tracked
- PDF URLs stored in invoice and logs

## Generation Status
- **SUCCESS**: PDF generated and stored
- **FAILED**: Generation error occurred
- **PENDING**: Generation in progress

## Integration Points
- **Invoice Service**: Endpoint for PDF generation
- **Audit Service**: Logs all PDF generation attempts

## Future Enhancements
- Multiple template types
- Custom branding per client
- Batch generation
- PDF storage in cloud (S3, etc.)

