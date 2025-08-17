# SmartClaims360 Architecture Documentation

## Overview

SmartClaims360 is a modern Spring Boot application designed for comprehensive claim management with AI-assisted features. The application provides both traditional CRUD operations for claims and advanced AI-powered capabilities including validation, fraud detection, summarization, and routing.

## Technology Stack

### Core Framework
- **Spring Boot**: 3.1.2 (Jakarta EE based)
- **Java**: 17 (Temurin JDK)
- **Build Tool**: Gradle 8.2.1
- **Package Manager**: Gradle Wrapper

### Dependencies
- **Spring Web**: REST API endpoints and web layer
- **Spring Data JPA**: Database abstraction and ORM
- **Spring Boot Validation**: Request validation using Jakarta validation
- **H2 Database**: In-memory database for development
- **Lombok**: Boilerplate code reduction
- **Spring Boot Test**: Comprehensive testing framework

## Module Structure

### Main Application
```
src/main/java/com/smartclaims360/smartclaims360/
├── SmartClaims360Application.java          # Main Spring Boot application
├── controller/
│   └── ClaimController.java                # Traditional CRUD endpoints
├── ai/
│   ├── controller/
│   │   └── AiController.java               # AI-powered endpoints
│   ├── service/
│   │   ├── AiValidationService.java        # Rule-based + LLM validation
│   │   ├── FraudScoringService.java        # Statistical fraud detection
│   │   ├── SummarizationService.java       # Claim summarization
│   │   └── RoutingService.java             # Intelligent routing
│   ├── dto/
│   │   ├── FraudScoreResponse.java         # Fraud scoring response
│   │   ├── ValidationResponse.java         # Validation result response
│   │   └── RoutingSuggestion.java          # Routing recommendation
│   └── provider/
│       ├── LlmValidationProvider.java      # LLM integration interface
│       └── MockLlmValidationProvider.java  # Mock LLM implementation
├── dto/
│   └── ClaimRequest.java                   # Claim creation request
├── entity/
│   └── Claim.java                          # JPA entity with fraud score
├── service/
│   └── ClaimService.java                   # Business logic layer
├── repository/
│   └── ClaimRepository.java                # Data access layer
└── exception/
    └── ClaimNotFoundException.java         # Custom exception handling
```

### Test Structure
```
src/test/java/com/smartclaims360/smartclaims360/
├── SmartClaims360ApplicationTests.java     # Application context tests
├── controller/
│   └── ClaimControllerTest.java            # Unit tests for CRUD endpoints
├── ai/
│   ├── controller/
│   │   └── AiControllerTest.java           # Unit tests for AI endpoints
│   ├── service/
│   │   ├── AiValidationServiceTest.java    # AI validation tests
│   │   ├── FraudScoringServiceTest.java    # Fraud scoring tests
│   │   ├── SummarizationServiceTest.java   # Summarization tests
│   │   └── RoutingServiceTest.java         # Routing tests
│   └── integration/
│       └── AiIntegrationTest.java          # End-to-end AI feature tests
└── integration/
    └── ClaimIntegrationTest.java           # End-to-end CRUD tests
```

## API Endpoints

### Traditional Claim Management
- `GET /health` - Health check endpoint
- `POST /claims` - Create new claim
- `GET /claims` - List all claims
- `GET /claims/{id}` - Get claim by ID

### AI-Assisted Features
- `POST /claims/validate` - Validate claim with AI hints
- `POST /claims/score` - Calculate fraud risk score
- `GET /claims/{id}/summary` - Generate claim summary
- `GET /claims/{id}/route` - Get routing suggestions

## Data Model

### Claim Entity
```java
@Entity
public class Claim {
    private UUID id;                    // Primary key
    private String claimantName;        // Claimant identifier
    private BigDecimal claimAmount;     // Claim monetary value
    private String claimType;           // AUTO, HEALTH, PROPERTY, LIFE
    private String status;              // Processing status (default: NEW)
    private LocalDateTime createdAt;    // Creation timestamp
    private BigDecimal fraudScore;      // AI-calculated fraud risk (0.0-1.0)
}
```

## AI Services Architecture

### Validation Service
- **Rule-based validation**: Name, amount, and type constraints
- **LLM integration**: Pluggable provider interface for future AI integration
- **Mock implementation**: Default provider for development/testing

### Fraud Scoring Service
- **Statistical analysis**: Z-score calculation for amount anomalies
- **Pattern detection**: Claim type frequency analysis
- **Risk assessment**: Weighted scoring algorithm (0.0-1.0 scale)
- **Historical data**: Leverages claim repository for baseline calculations

### Summarization Service
- **Comprehensive summaries**: Key claim details with risk analysis
- **Integration**: Combines validation and fraud scoring results
- **Configurable**: Feature flag controlled (ai.summarization.enabled)

### Routing Service
- **Intelligent routing**: Queue suggestions based on claim type and risk
- **Risk-based logic**: High fraud scores route to manual review
- **Configurable thresholds**: Customizable risk level boundaries

## Configuration

### Feature Flags (application.yml)
```yaml
ai:
  validation:
    enabled: true
  scoring:
    enabled: true
  summarization:
    enabled: true
  routing:
    enabled: true
```

### Database Configuration
- **Development**: H2 in-memory database
- **Production**: PostgreSQL (configurable)
- **JPA**: Hibernate with automatic schema generation

## CI/CD Pipeline

### GitHub Actions Workflow
- **Java 17**: Temurin JDK setup
- **Gradle caching**: Dependencies cached for performance
- **Build verification**: `./gradlew clean build`
- **Test execution**: Full test suite with coverage
- **Artifact publishing**: Build and test results

## Known Risks and Considerations

### Current State Assessment
- ✅ **Build Status**: All 41 tests passing, clean build
- ✅ **Dependencies**: Modern, secure versions
- ✅ **Jakarta Migration**: Already completed (no javax.* imports)
- ✅ **Java 17**: Latest LTS version in use
- ✅ **Test Coverage**: Comprehensive unit and integration tests

### Identified Opportunities
- **Spring Boot Version**: Can upgrade from 3.1.2 to 3.5.4 (latest stable)
- **Gradle Version**: Can upgrade from 8.2.1 to 8.14.3 (latest stable)
- **API Documentation**: Missing Swagger/OpenAPI integration
- **Governance**: Missing CODEOWNERS and Dependabot configuration

### Risk Mitigation
- **Incremental upgrades**: Phased approach with rollback plans
- **Comprehensive testing**: Maintain existing test coverage
- **Feature flags**: AI services can be disabled if issues arise
- **Mock implementations**: Reduce external dependencies during development

## Performance Characteristics

### Fraud Scoring Performance
- **Algorithm complexity**: O(n) where n = historical claims count
- **Memory usage**: Loads all historical claims for statistical analysis
- **Optimization opportunity**: Consider caching statistical baselines

### Database Performance
- **H2 limitations**: In-memory database, data loss on restart
- **Production readiness**: PostgreSQL recommended for production
- **Indexing**: Consider indexes on claimType and createdAt for analytics

## Security Considerations

- **Input validation**: Jakarta validation annotations on all DTOs
- **Exception handling**: Custom exceptions prevent information leakage
- **Database security**: Prepared statements via JPA prevent SQL injection
- **Configuration**: Sensitive data should use environment variables

## Extensibility Points

### LLM Integration
- **Interface-based design**: LlmValidationProvider allows easy swapping
- **Mock implementation**: Development-friendly default provider
- **Future integration**: Ready for OpenAI, Azure AI, or custom LLM services

### AI Service Extension
- **Service-oriented architecture**: Each AI capability is independently configurable
- **Feature flags**: Individual services can be enabled/disabled
- **Plugin architecture**: New AI services can be added following existing patterns

## Monitoring and Observability

### Current Capabilities
- **Health endpoint**: Basic application health check
- **Spring Boot Actuator**: Not currently enabled (opportunity for enhancement)
- **Logging**: Standard Spring Boot logging configuration

### Enhancement Opportunities
- **Metrics collection**: Fraud score distributions, validation success rates
- **Performance monitoring**: Endpoint response times, database query performance
- **Business metrics**: Claim processing volumes, AI service utilization
