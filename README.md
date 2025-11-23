# Billing and Payments API

A comprehensive Spring Boot backend system for managing invoices, shipments, and billing operations with microservices architecture simulation.

## 🏗️ Architecture Overview

This project implements a **modular microservices architecture** following SOLID principles:

- **Single Responsibility Principle (SRP)**: Each class has one reason to change
- **Open/Closed Principle (OCP)**: Open for extension, closed for modification
- **Liskov Substitution Principle (LSP)**: Derived classes are substitutable for base classes
- **Interface Segregation Principle (ISP)**: Clients shouldn't depend on interfaces they don't use
- **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions

### Microservices Modules

The project simulates microservices by organizing functionality into separate modules:

1. **Invoice Service** - Invoice creation, editing, issuance, and PDF generation
2. **Shipment Service** - Shipment tracking and management
3. **Audit Service** - Version history and audit logging
4. **PDF Service** - Document generation and management

## 🚀 Features

### Core Functionality
- JWT-based authentication with existing infrastructure
- Invoice lifecycle management (Draft → Issued → Paid)
- Shipment tracking and integration
- Comprehensive audit logging
- PDF generation for invoices
- Optimistic concurrency control
- Version history and revert capability

### Technical Features
- RESTful API with versioning (v1)
- OpenAPI/Swagger documentation
- Database migrations with Flyway
- PostgreSQL database
- Input validation and error handling
- Transaction management
- Complete audit trail

## 📁 Project Structure

```
src/main/java/com/fabrica/p6f5/springapp/
├── config/
│   ├── SecurityConfig.java              # Security configuration
│   └── OpenApiConfig.java               # Swagger documentation config
├── controller/
│   ├── AuthController.java              # Authentication endpoints
│   └── UserController.java              # User management endpoints
├── invoice/                              # Invoice Service
│   ├── controller/
│   │   ├── InvoiceController.java       # Invoice endpoints
│   │   └── InvoiceHistoryController.java # History endpoints
│   ├── service/
│   │   └── InvoiceService.java          # Invoice business logic
│   ├── repository/
│   │   ├── InvoiceRepository.java
│   │   ├── InvoiceItemRepository.java
│   │   └── InvoiceShipmentRepository.java
│   ├── model/
│   │   ├── Invoice.java
│   │   ├── InvoiceItem.java
│   │   └── InvoiceShipment.java
│   ├── dto/
│   │   ├── CreateInvoiceRequest.java
│   │   ├── UpdateInvoiceRequest.java
│   │   ├── InvoiceResponse.java
│   │   └── InvoiceHistoryResponse.java
│   └── README.md                        # Service documentation
├── shipment/                             # Shipment Service
│   ├── controller/
│   │   └── ShipmentController.java      # Shipment endpoints
│   ├── service/
│   │   └── ShipmentService.java         # Shipment business logic
│   ├── repository/
│   │   └── ShipmentRepository.java
│   ├── model/
│   │   └── Shipment.java
│   └── README.md                        # Service documentation
├── audit/                                # Audit Service
│   ├── service/
│   │   └── AuditService.java            # Audit logging
│   ├── repository/
│   │   ├── AuditLogRepository.java
│   │   └── InvoiceHistoryRepository.java
│   ├── model/
│   │   ├── AuditLog.java
│   │   └── InvoiceHistory.java
│   └── README.md                        # Service documentation
├── pdf/                                  # PDF Service
│   ├── service/
│   │   └── PdfService.java              # PDF generation
│   ├── repository/
│   │   └── PdfLogRepository.java
│   ├── model/
│   │   └── PdfLog.java
│   └── README.md                        # Service documentation
├── exception/
│   ├── GlobalExceptionHandler.java      # Global error handling
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
├── dto/
│   └── ApiResponse.java                 # Standard API response
├── entity/
│   └── User.java                        # User entity
├── repository/
│   └── UserRepository.java              # User repository
├── security/
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
├── service/
│   ├── AuthService.java                 # Authentication service
│   ├── JwtService.java                  # JWT operations
│   └── UserService.java                 # User management
└── SpringappApplication.java            # Main application class

src/main/resources/
├── application.properties               # Application configuration
├── db/migration/                         # Flyway migrations
│   └── V3__create_invoice_system.sql    # Database schema
└── [graphql schemas if using GraphQL]
```

## 🛠️ Setup Instructions

### Prerequisites
- Java 21
- PostgreSQL database
- Gradle 8.x or higher

### 1. Database Configuration

The application uses PostgreSQL. Update the connection in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://your-database-url
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Running the Application

```bash
./gradlew clean build
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 3. Access Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **GraphiQL**: http://localhost:8080/graphiql (if using GraphQL)

## 📚 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "password123"
}
```

### Invoice Endpoints (v1)

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
      "unitPrice": 100.00
    }
  ],
  "shipmentIds": [1, 2],
  "taxAmount": 50.00,
  "currency": "USD"
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
  "version": 1,
  "taxAmount": 60.00
}
```

#### Issue Invoice
```http
POST /api/v1/invoices/{invoiceId}/issue
Authorization: Bearer {token}
```

#### Generate PDF
```http
POST /api/v1/invoices/{invoiceId}/pdf
Authorization: Bearer {token}
```

#### Get Invoice History
```http
GET /api/v1/invoices/{invoiceId}/history
Authorization: Bearer {token}
```

### Shipment Endpoints (v1)

#### Get All Shipments
```http
GET /api/v1/shipments
Authorization: Bearer {token}
```

#### Get Unlinked Shipments
```http
GET /api/v1/shipments/unlinked
Authorization: Bearer {token}
```

## 🎯 User Stories Implemented

### HU-002: Create Invoice ✅
- Create draft invoices in "Draft" state
- Link one or more shipments
- Validate required fields
- Prefill data from shipments
- Prevent duplicates per shipment

### HU-003: Edit Draft Invoice ✅
- Edit invoices with optimistic concurrency control
- Conflict detection via version field
- Change history and revert capability
- Prevent editing when not in Draft state
- Full audit trail

### HU-004: Issue Invoice ✅
- Transition from Draft to Issued
- Fiscal validations
- Generate unique fiscal number
- Lock further edits
- Audit and trace issuance events

### HU-005: Generate Invoice PDF ✅
- Generate PDFs using templates
- Handle generation failures gracefully
- Apply template based on client type
- Log all generation attempts

## 🗄️ Database Schema

The system uses the following main tables:

- **invoices** - Invoice master data
- **invoice_items** - Line items
- **invoice_shipments** - Invoice-shipment links
- **shipments** - Shipment data
- **audit_logs** - System-wide audit trail
- **invoice_history** - Invoice version history
- **pdf_logs** - PDF generation tracking

See migration file `V3__create_invoice_system.sql` for complete schema.

## 🔐 Security Features

- JWT-based authentication
- BCrypt password encryption
- Role-based access control
- Endpoint protection
- Input validation
- SQL injection prevention
- XSS protection

## 🧪 Testing the API

### Example Flow

1. **Register a user**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

2. **Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"password123"}'
```

3. **Create an invoice**:
```bash
curl -X POST http://localhost:8080/api/v1/invoices \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Test Client",
    "invoiceDate": "2024-01-15",
    "dueDate": "2024-02-15",
    "items": [{"description": "Service", "quantity": 1, "unitPrice": 100}],
    "taxAmount": 10
  }'
```

4. **Issue the invoice**:
```bash
curl -X POST http://localhost:8080/api/v1/invoices/{invoiceId}/issue \
  -H "Authorization: Bearer {token}"
```

5. **Generate PDF**:
```bash
curl -X POST http://localhost:8080/api/v1/invoices/{invoiceId}/pdf \
  -H "Authorization: Bearer {token}"
```

## 🚨 Error Handling

The API returns consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": 1234567890
}
```

Common error codes:
- 400 - Bad Request (validation errors)
- 401 - Unauthorized (invalid/missing token)
- 404 - Not Found (resource not found)
- 409 - Conflict (optimistic locking)
- 500 - Internal Server Error

## 📝 SOLID Principles Implementation

### Single Responsibility Principle
- Each service handles one domain (Invoice, Shipment, Audit, PDF)
- Controllers only handle HTTP concerns
- Services contain business logic
- Repositories handle data access

### Open/Closed Principle
- Controllers can be extended without modification
- Services use dependency injection
- Interfaces allow for multiple implementations

### Liskov Substitution Principle
- Repository interfaces with Spring Data JPA
- Service interfaces can be substituted

### Interface Segregation Principle
- Focused repository interfaces
- Single-purpose service methods

### Dependency Inversion Principle
- Controllers depend on service interfaces
- Services depend on repository interfaces
- Spring's IoC container manages dependencies

## 🔍 Monitoring and Logging

- Comprehensive audit logging
- Request/response logging
- Error tracking
- Performance monitoring
- SQL query logging (dev mode)

## 🚀 Deployment

For production:
1. Update database configuration
2. Set secure JWT secret
3. Configure CORS for frontend domain
4. Set up SSL/TLS for HTTPS
5. Configure proper logging levels
6. Set up monitoring and alerting

## 📞 Support

For issues or questions:
- Check service-specific README files
- Review API documentation at `/swagger-ui.html`
- Check application logs
- Verify database migrations completed

## 📄 License

MIT License - See LICENSE file for details
