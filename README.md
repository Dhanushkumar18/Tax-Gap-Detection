# Tax Gap Detection & Compliance Validation Service

A comprehensive backend service for tax auditors to verify the accuracy of financial transactions, compute tax gaps, execute rule-based compliance checks, and generate audit reports.

## Features

- **Transaction Validation**: Validate financial transactions with comprehensive error handling
- **Tax Gap Calculation**: Automatically compute expected tax and identify tax discrepancies
- **Configurable Rule Engine**: Database-driven rules for tax compliance checks
- **Exception Management**: Track and filter compliance exceptions
- **Audit Logging**: Complete audit trail for all operations
- **Reporting**: Generate tax summaries and exception reports
- **Spring Security**: Role-based access control (ADMIN, AUDITOR)

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security**
- **Spring Data JPA / Hibernate**
- **H2 Database** (for development)
- **MySQL / PostgreSQL** (for production)
- **Maven**
- **JUnit 5 & Mockito** (Testing)

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Installation & Run

```bash
# Clone repository
git clone <repository-url>
cd Tax-Gap-Detection

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Application starts on `http://localhost:8080`

## Authentication

Default credentials:
- **Username**: admin / auditor1 / auditor2
- **Password**: password
- **Roles**: ROLE_ADMIN, ROLE_AUDITOR

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/transactions/upload-batch` | Upload transaction batch |
| GET | `/api/v1/transactions/{id}` | Get transaction |
| GET | `/api/v1/transactions/customer/{customerId}` | Get customer transactions |
| GET | `/api/v1/exceptions` | Get all exceptions |
| GET | `/api/v1/exceptions/customer/{customerId}` | Get customer exceptions |
| GET | `/api/v1/exceptions/severity/{severity}` | Filter by severity |
| GET | `/api/v1/exceptions/rule/{ruleName}` | Filter by rule |
| GET | `/api/v1/reports/customer-tax-summary/{customerId}` | Tax summary report |
| GET | `/api/v1/reports/exception-summary` | Exception summary |

## cURL Examples

### Upload Batch
```bash
curl -X POST http://localhost:8080/api/v1/transactions/upload-batch \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ=" \
  -d '{
    "transactions": [
      {
        "transactionId": "TXN001",
        "date": "2024-01-15",
        "customerId": 1001,
        "amount": "50000.00",
        "taxRate": "0.18",
        "reportedTax": "9000.00",
        "transactionType": "SALE"
      }
    ]
  }'
```

### Get Tax Summary
```bash
curl http://localhost:8080/api/v1/reports/customer-tax-summary/1001 \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

### Get Exceptions
```bash
curl http://localhost:8080/api/v1/exceptions \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

## Testing

```bash
# Run all tests
mvn test

# Generate coverage report
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
```

## Architecture

### Layered Architecture
- **Controller**: REST API endpoints
- **Service**: Business logic & workflows
- **Engine**: Rule execution & tax calculations
- **Repository**: Data access layer
- **Entity**: Domain models

### Core Components

1. **TransactionProcessingService**: Orchestrates transaction workflow
2. **TaxCalculationService**: Computes tax gaps and compliance status
3. **RuleEngineService**: Executes all active compliance rules
4. **ExceptionManagementService**: Creates and manages exceptions
5. **AuditLoggingService**: Maintains complete audit trail
6. **ReportingService**: Generates tax and exception reports

## Database Configuration

### Development (H2 - Default)
Configured in `application.yml` - no additional setup needed.

### Production (MySQL)
Update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taxgapdb
    username: root
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### Production (PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taxgapdb
    username: postgres
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## Tax Compliance Rules

### 1. High-Value Transaction Rule
Flags transactions where amount exceeds configured threshold (default: 100,000).

### 2. GST Slab Violation Rule
Flags high-amount transactions (>50,000) with tax rate below required 18%.

### 3. Refund Validation Rule
Validates refund transaction compliance.

## Tax Gap Determination

- **|taxGap| ≤ 1**: COMPLIANT
- **taxGap > 1**: UNDERPAID
- **taxGap < -1**: OVERPAID
- **reportedTax missing**: NON_COMPLIANT

Where: `taxGap = expectedTax - reportedTax` and `expectedTax = amount × taxRate`

## Compliance Score

$$\text{Compliance Score} = 100 - \left(\frac{\text{Non-Compliant Transactions}}{\text{Total Transactions}} \times 100\right)$$

## Test Coverage

Current coverage target: **40-50%** of service and engine layers.

### Test Classes
- `TransactionValidationServiceTest` - Validation logic
- `TaxCalculationServiceTest` - Tax calculations
- `RuleExecutorTest` - Rule execution
- `TransactionProcessingServiceTest` - Integration tests

Run tests: `mvn test`
Generate report: `mvn clean test jacoco:report`

## Project Structure

```
src/
├── main/java/com/taxgap/
│   ├── config/              # Spring Security & UserDetailsService
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic layer
│   ├── engine/              # Rule engine implementation
│   ├── repository/          # JPA repository interfaces
│   ├── entity/              # JPA domain entities
│   ├── dto/                 # Data transfer objects
│   ├── exception/           # Custom exceptions
│   └── util/                # Utility classes (DTOMapper)
└── test/java/com/taxgap/    # Unit tests
```

## Key Transaction Fields

| Field | Type | Description |
|-------|------|-------------|
| transactionId | String | Unique identifier |
| date | LocalDate | Transaction date |
| customerId | Long | Customer identifier |
| amount | BigDecimal | Transaction amount |
| taxRate | BigDecimal | Tax rate (0-1) |
| reportedTax | BigDecimal | Tax reported by customer |
| transactionType | Enum | SALE, REFUND, EXPENSE |

## Database Schema Overview

- **transactions**: Core transaction data and calculations
- **exceptions**: Compliance exceptions and violations
- **audit_logs**: Complete operation audit trail
- **tax_rules**: Database-driven compliance rules
- **users**: Authentication and role management

## Performance Considerations

- Batch transaction processing for efficiency
- Database indexes on frequently queried columns
- Optimized reporting queries
- Stateless authentication (no session overhead)

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 in use | Change in `application.yml` |
| Auth failed | Verify credentials in users table |
| Rule not executing | Check `enabled` flag in tax_rules |
| H2 console not accessible | Ensure `/h2-console` is not blocked |

## Development Workflow

1. Create feature branch
2. Implement changes with tests
3. Run `mvn test` to verify
4. Run `mvn clean install` for full build
5. Submit pull request

## License

MIT License

## Support

Create an issue in the GitHub repository for questions or bugs.