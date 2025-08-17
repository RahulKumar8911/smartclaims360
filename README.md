# SmartClaims360

**SmartClaims360** is an AI-assisted, full-stack claims processing platform that automates and optimizes the claims lifecycle from intake to settlement. It integrates AI-driven validation, fraud detection, intelligent routing, and real-time dashboards to deliver faster, more accurate, and more transparent claim handling.

---

## 🚀 Features

1. **Multi-Source Claim Ingestion**  
   - Upload via portal  
   - API integration with external systems  
   - Email parsing and extraction  

2. **AI-Powered Validation**  
   - Detects missing or incorrect claim data  
   - Suggests corrections before submission  

3. **Intelligent Routing**  
   - Automatically assigns claims to the correct department or adjuster  
   - Uses rules + AI models for priority handling  

4. **Fraud Detection**  
   - Anomaly detection on historical claim patterns  
   - Real-time fraud scoring for incoming claims  

5. **Claims Summarization**  
   - Generates concise summaries for adjusters  
   - Highlights critical details and red flags  

6. **Workflow Integration**  
   - Connects to Jira, Slack, and email for notifications and updates  

7. **Analytics Dashboard**  
   - View status of claims in real time  
   - Track turnaround times, backlog, and fraud detection stats  

---

## 🛠 Tech Stack

**Frontend:**  
- React / Remix  
- Tailwind CSS  

**Backend:**  
- Java Spring Boot (current implementation)
- RESTful APIs + WebSockets for real-time updates  

**Database:**  
- H2 Database (development)
- PostgreSQL (production)  
- Redis (caching)  

**AI / ML:**  
- OpenAI API / Hugging Face Transformers (NLP for validation & summarization)  
- Scikit-learn / PyTorch (fraud detection models)  

**Infrastructure:**  
- Docker & Kubernetes  
- GitHub Actions CI/CD  
- SonarQube for code quality checks  

---

## 📐 High-Level Architecture

```plaintext
[User Portal / API] ---> [Backend Service Layer] ---> [Database]
       |                         |                         |
       |                         |--> [AI Validation Engine]
       |                         |--> [Fraud Detection Model]
       |                         |--> [Workflow Integrations]
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** (Temurin JDK recommended)
- **Git** for version control

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/RahulKumar8911/smartclaims360.git
   cd smartclaims360
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run tests:**
   ```bash
   ./gradlew test
   ```

4. **Start the application:**
   ```bash
   ./gradlew bootRun
   ```

5. **Verify the application is running:**
   ```bash
   curl http://localhost:8081/health
   ```
   Expected response: `SmartClaims360 API is running`

### Available Endpoints

#### Health Check
- **Health Check:** `GET /health` - Returns application status

#### Claims API
- **Create Claim:** `POST /claims` - Create a new claim
- **Get All Claims:** `GET /claims` - Retrieve all claims
- **Get Claim by ID:** `GET /claims/{id}` - Retrieve a specific claim by UUID

#### AI-Assisted Features
- **Validate Claim:** `POST /claims/validate` - Validate claim with AI assistance
- **Score Claim:** `POST /claims/score` - Get fraud risk score for claim
- **Claim Summary:** `GET /claims/{id}/summary` - Get comprehensive claim summary
- **Routing Suggestion:** `GET /claims/{id}/route` - Get processing queue suggestion

#### Database Console
- **H2 Console:** `http://localhost:8081/h2-console` - Database management interface
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

### API Usage Examples

#### Create a new claim:
```bash
curl -X POST http://localhost:8081/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 1500.00,
    "claimType": "AUTO"
  }'
```

Expected response (201 Created):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "claimantName": "John Doe",
  "claimAmount": 1500.00,
  "claimType": "AUTO",
  "status": "NEW",
  "createdAt": "2025-08-17T13:21:00"
}
```

#### Get all claims:
```bash
curl http://localhost:8081/claims
```

Expected response (200 OK):
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "claimantName": "John Doe",
    "claimAmount": 1500.00,
    "claimType": "AUTO",
    "status": "NEW",
    "createdAt": "2025-08-17T13:21:00"
  }
]
```

#### Get claim by ID:
```bash
curl http://localhost:8081/claims/123e4567-e89b-12d3-a456-426614174000
```

Expected response (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "claimantName": "John Doe",
  "claimAmount": 1500.00,
  "claimType": "AUTO",
  "status": "NEW",
  "createdAt": "2025-08-17T13:21:00"
}
```

#### Validation Examples

Invalid request (blank claimant name):
```bash
curl -X POST http://localhost:8081/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "",
    "claimAmount": 1500.00,
    "claimType": "AUTO"
  }'
```
Response: 400 Bad Request with validation errors

Invalid request (negative amount):
```bash
curl -X POST http://localhost:8081/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": -100.00,
    "claimType": "AUTO"
  }'
```
Response: 400 Bad Request with validation errors

Claim not found:
```bash
curl http://localhost:8081/claims/00000000-0000-0000-0000-000000000000
```
Response: 404 Not Found with message "Claim not found with id: 00000000-0000-0000-0000-000000000000"

### AI Features API Examples

#### Validate a claim:
```bash
curl -X POST http://localhost:8081/claims/validate \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 15000.00,
    "claimType": "AUTO"
  }'
```

Expected response (200 OK):
```json
{
  "valid": true,
  "reasons": [],
  "llmHints": [
    "High claim amount detected - consider additional documentation"
  ]
}
```

#### Score a claim for fraud risk:
```bash
curl -X POST http://localhost:8081/claims/score \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 50000.00,
    "claimType": "LIFE"
  }'
```

Expected response (200 OK):
```json
{
  "fraudScore": 0.65,
  "riskLevel": "MEDIUM",
  "explanation": "Medium fraud risk - some unusual patterns detected that warrant additional review"
}
```

#### Get claim summary:
```bash
curl http://localhost:8081/claims/123e4567-e89b-12d3-a456-426614174000/summary
```

Expected response (200 OK):
```
CLAIM SUMMARY
=============
ID: 123e4567-e89b-12d3-a456-426614174000
Claimant: John Doe
Amount: $1,500.00
Type: AUTO
Status: NEW
Created: 2025-08-17T13:21:00

VALIDATION STATUS: VALID
AI Recommendations:
- High claim amount detected - consider additional documentation

FRAUD RISK: LOW
Fraud Score: 0.25/1.00
Risk Analysis: Low fraud risk - claim appears consistent with historical patterns
```

#### Get routing suggestion:
```bash
curl http://localhost:8081/claims/123e4567-e89b-12d3-a456-426614174000/route
```

Expected response (200 OK):
```json
{
  "queue": "AUTO",
  "reason": "Standard processing for AUTO claim with low fraud risk (score: 0.25)"
}
```

#### Invalid claim validation:
```bash
curl -X POST http://localhost:8081/claims/validate \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "",
    "claimAmount": -100.00,
    "claimType": "INVALID"
  }'
```

Expected response (200 OK):
```json
{
  "valid": false,
  "reasons": [
    "Claimant name cannot be blank",
    "Claim amount must be greater than 0",
    "Claim type must be one of: AUTO, HEALTH, PROPERTY, LIFE"
  ],
  "llmHints": []
}
```

### Configuration

AI features can be controlled via `application.yml`:

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

## How to Access Swagger UI

Once the application is running, you can access the interactive API documentation at:

**Swagger UI**: http://localhost:8081/swagger-ui.html
**OpenAPI Spec**: http://localhost:8081/api-docs

The Swagger UI provides:
- Interactive API testing for all 8 endpoints
- Complete request/response examples with realistic data
- Schema documentation for all DTOs and entities
- Error response examples and status codes
- AI-specific examples showing fraud scores and validation hints

## API Endpoints with Examples

### Traditional Claim Management

#### Health Check
```bash
curl -X GET http://localhost:8081/health
# Response: "SmartClaims360 API is running"
```

#### Create Claim
```bash
curl -X POST http://localhost:8081/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 1500.00,
    "claimType": "AUTO"
  }'

# Example Response:
# {
#   "id": "123e4567-e89b-12d3-a456-426614174000",
#   "claimantName": "John Doe",
#   "claimAmount": 1500.00,
#   "claimType": "AUTO",
#   "status": "NEW",
#   "createdAt": "2025-08-17T14:30:00",
#   "fraudScore": null
# }
```

#### Get All Claims
```bash
curl -X GET http://localhost:8081/claims

# Example Response:
# [
#   {
#     "id": "123e4567-e89b-12d3-a456-426614174000",
#     "claimantName": "John Doe",
#     "claimAmount": 1500.00,
#     "claimType": "AUTO",
#     "status": "NEW",
#     "createdAt": "2025-08-17T14:30:00",
#     "fraudScore": 0.25
#   }
# ]
```

#### Get Claim by ID
```bash
curl -X GET http://localhost:8081/claims/{claim-id}

# Example Response: Same as individual claim object above
```

### AI-Assisted Features

#### Validate Claim
```bash
curl -X POST http://localhost:8081/claims/validate \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 1500.00,
    "claimType": "AUTO"
  }'

# Example Response:
# {
#   "valid": true,
#   "reasons": [],
#   "llmHints": [
#     "Consider adding additional documentation for faster processing",
#     "Claim amount is within normal range for AUTO claims"
#   ]
# }
```

#### Score Claim for Fraud
```bash
curl -X POST http://localhost:8081/claims/score \
  -H "Content-Type: application/json" \
  -d '{
    "claimantName": "John Doe",
    "claimAmount": 15000.00,
    "claimType": "AUTO"
  }'

# Example Response:
# {
#   "fraudScore": 0.75,
#   "riskLevel": "HIGH",
#   "explanation": "Claim amount significantly above average (z-score: 2.8). Requires manual review."
# }
```

#### Get Claim Summary
```bash
curl -X GET http://localhost:8081/claims/{claim-id}/summary

# Example Response:
# CLAIM SUMMARY
# =============
# Claim ID: 123e4567-e89b-12d3-a456-426614174000
# Claimant: John Doe
# Amount: $1,500.00
# Type: AUTO
# Status: NEW
# Created: 2025-08-17T14:30:00
# 
# VALIDATION STATUS
# ================
# Status: VALID
# AI Recommendations: Consider adding additional documentation
# 
# FRAUD RISK ANALYSIS
# ==================
# Fraud Score: 0.25 (LOW RISK)
# Risk Analysis: Claim amount within normal range
```

#### Get Routing Suggestion
```bash
curl -X GET http://localhost:8081/claims/{claim-id}/route

# Example Response:
# {
#   "queue": "AUTO",
#   "reason": "Standard AUTO claim with low fraud risk (0.25). Route to automated processing."
# }
```

### Development

The application uses:
- **Spring Boot 3.5.4** with Java 17
- **Gradle 8.14.3** for build management
- **H2 Database** for development (in-memory)
- **Spring Data JPA** for data persistence
- **Lombok** for reducing boilerplate code
- **Swagger/OpenAPI 3** for comprehensive API documentation

### AI Features

SmartClaims360 includes AI-assisted validation and fraud scoring capabilities:

**Architecture:**
- **AI Package Structure:** All AI services are organized in `com.smartclaims360.ai`
- **Pluggable LLM Interface:** `LlmValidationProvider` allows easy integration of real LLM services
- **Feature Flags:** Enable/disable AI features via configuration
- **Statistical Analysis:** Fraud scoring uses z-score analysis and pattern detection

**AI Services:**
- **AiValidationService:** Rule-based validation with LLM hints
- **FraudScoringService:** Anomaly detection using statistical analysis
- **SummarizationService:** Generates comprehensive claim summaries
- **RoutingService:** Suggests processing queues based on risk assessment

### CI/CD

The project includes GitHub Actions workflow that:
- Builds the project with Java 17
- Runs all tests
- Caches Gradle dependencies for faster builds

---

## 📝 Project Structure

```
smartclaims360/
├── src/
│   ├── main/
│   │   ├── java/com/smartclaims360/smartclaims360/
│   │   │   ├── SmartClaims360Application.java
│   │   │   └── controller/
│   │   │       └── ClaimController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/smartclaims360/smartclaims360/
│           └── SmartClaims360ApplicationTests.java
├── .github/workflows/
│   └── ci.yml
├── build.gradle
├── gradlew
└── README.md
