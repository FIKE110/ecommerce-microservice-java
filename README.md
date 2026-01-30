# Ecommerce Microservices Platform

A comprehensive, production-ready microservices-based e-commerce platform built with Spring Boot, Spring Cloud, and Next.js.

## Overview

This platform implements a full-featured e-commerce system with separate microservices handling different business domains. The architecture follows best practices for distributed systems including service discovery, centralized configuration, API gateway, distributed tracing, and real-time monitoring.

## Features

### Business Capabilities
- **Authentication & Authorization**: User registration, login, JWT token management, email verification
- **Product Management**: Product catalog with categories, pricing, search, and filtering
- **Inventory Management**: Stock tracking, reservation, and availability checks
- **Shopping Cart**: Redis-based cart with real-time updates
- **Order Processing**: Order creation, status tracking, history management
- **Payment Integration**: Integration with payment providers, checkout flows
- **Customer Profiles**: User profile management, preferences
- **Notifications**: Email/SMS notifications for order updates

### Technical Features
- **Service Discovery**: Netflix Eureka for dynamic service registration
- **API Gateway**: Spring Cloud Gateway for unified API entry point
- **Centralized Configuration**: Spring Cloud Config for externalized configuration
- **Distributed Tracing**: Zipkin integration for request tracing across services
- **Monitoring**: Spring Boot Admin dashboard for service health monitoring
- **Circuit Breaking**: Resilience patterns for fault tolerance
- **Message Queue**: RabbitMQ for asynchronous communication
- **Caching**: Redis for cart and session management
- **API Documentation**: OpenAPI/Swagger for all services

## Tech Stack

### Backend Services
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.3
- **Cloud Framework**: Spring Cloud 2025.0.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway (WebFlux)
- **Database**: PostgreSQL 42.7.7
- **Cache**: Redis
- **Message Queue**: RabbitMQ (Spring AMQP)
- **Security**: Spring Security + OAuth2 + JWT
- **Tracing**: Zipkin + Brave
- **Monitoring**: Spring Boot Admin 3.5.0
- **API Documentation**: SpringDoc OpenAPI 2.8.12
- **Build Tool**: Maven 3.8+
- **Testing**: JUnit, Mockito, Testcontainers

### Frontend Application
- **Framework**: Next.js 16.0.0
- **UI Library**: React 19.2.0
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4.1.9
- **State Management**: Zustand 5.0.8
- **HTTP Client**: Axios
- **Components**: Radix UI primitives
- **Form Handling**: React Hook Form + Zod validation
- **Package Manager**: pnpm/bun

## Architecture & Design

### Microservices Architecture

The system follows a microservices architecture with bounded contexts for each business domain:

```
┌─────────────┐     ┌─────────────┐
│   Webapp    │────▶│ API Gateway │
│   (Next.js) │     │   (8081)    │
└─────────────┘     └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Auth Service │   │ Product Svc  │   │  Order Svc   │
│    (8080)    │   │   (8082)     │   │   (8084)     │
└──────────────┘   └──────────────┘   └──────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌─────────────────────────────────────────────────┐
│           Service Discovery (8761)               │
└─────────────────────────────────────────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Config Srv   │   │  Cart Svc    │   │ Payment Svc  │
│   (8888)     │   │   (8087)     │   │   (8085)     │
└──────────────┘   └──────────────┘   └──────────────┘
        │
        ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│Admin Server  │   │ Customer Svc │   │ Inventory Svc│
│   (9090)     │   │   (8090)     │   │   (8083)     │
└──────────────┘   └──────────────┘   └──────────────┘
```

### Communication Patterns

- **Synchronous**: REST APIs between services (via API Gateway)
- **Asynchronous**: RabbitMQ for event-driven communication (order processing, notifications)
- **Service-to-Service**: OpenFeign for internal service calls

### Data Management

Each microservice manages its own database (database-per-service pattern):
- Auth Service: User credentials, tokens
- Customer Service: User profiles
- Product Service: Product catalog
- Inventory Service: Stock levels
- Order Service: Order records
- Cart Service: Redis-based shopping carts
- Payment Service: Payment transactions

## Project Structure

```
ecommerce/
├── admin-server/              # Spring Boot Admin monitoring dashboard
├── api-docs/                  # Aggregated API documentation portal
├── api-gateway/               # Spring Cloud Gateway (entry point)
├── auth-service/              # Authentication & JWT management
├── cart-service/              # Shopping cart with Redis
├── config-server/             # Centralized configuration
├── customer-service/          # Customer profile management
├── inventory-service/         # Stock management
├── notification-service/      # Email/SMS notifications
├── order-service/             # Order processing
├── payment-service/           # Payment integration
├── product-service/           # Product catalog
├── service-discovery/         # Netflix Eureka server
├── utils/                     # Shared utilities & common code
├── webapp/                    # Next.js frontend
├── docker-compose.yml         # Docker orchestration
├── pom.xml                    # Maven parent POM
├── Makefile                   # Build & deployment commands
└── logs/                      # Application logs
```

## Installation & Setup

### Prerequisites

Ensure you have the following installed:

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+**
- **pnpm or bun** (for frontend)
- **PostgreSQL** (optional - can use Docker)
- **Redis** (optional - can use Docker)
- **RabbitMQ** (optional - can use Docker)
- **Docker & Docker Compose** (recommended)

### Quick Start

#### Option 1: Using Make (Recommended)

```bash
# Install all dependencies
make install

# Start all services
make start

# Or use the dev command
make dev
```

#### Option 2: Using Docker Compose

```bash
# Start all services with Docker
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Option 3: Manual Startup

```bash
# Build all services
mvn clean install -DskipTests

# Start services in order (see startup sequence below)
cd service-discovery && mvn spring-boot:run
cd config-server && mvn spring-boot:run
# ... continue with other services
```

### Startup Sequence

Services must be started in the following order:

1. **Service Discovery** (8761) - Eureka server
2. **Config Server** (8888) - Configuration provider
3. **Admin Server** (9090) - Monitoring dashboard
4. **API Gateway** (8081) - Entry point
5. **Auth Service** (8080) - Authentication
6. **Business Services** - Product, Customer, Cart, Order, Payment, Inventory
7. **Notification Service** (8086) - Notifications
8. **Webapp** (3000) - Frontend

## Environment Variables

### Database Configuration

All services use the same PostgreSQL database (configured in application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
```

### Key Environment Variables

| Service | Variable | Default | Description |
|---------|----------|---------|-------------|
| All | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka` | Eureka server URL |
| All | `ZIPKIN_TRACING_ENDPOINT` | `http://localhost:9411/api/v2/spans` | Zipkin tracing URL |
| Auth Service | `RSA_PRIVATE_KEY` | `classpath:certs/private.pem` | JWT signing key |
| Auth Service | `RSA_PUBLIC_KEY` | `classpath:certs/public.pem` | JWT verification key |
| Webapp | `VITE_API_URL` | `http://localhost:8081` | API Gateway URL |

## Running the Project

### Starting All Services

```bash
# Using Make
make start

# Or using the shell script
chmod +x start-all.sh
./start-all.sh
```

### Starting Individual Services

```bash
# Start a specific service
cd auth-service
mvn spring-boot:run

# Start only backend services
make start-backend

# Start only frontend
make start-webapp
```

### Stopping Services

```bash
# Stop all services
make stop

# Or press Ctrl+C when using start-all.sh
```

### Viewing Logs

```bash
# View all logs
make logs

# View specific service log
tail -f logs/auth-service.log
tail -f logs/webapp.log
```

## Service URLs

After starting all services, access them at:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | Next.js web application |
| **API Gateway** | http://localhost:8081 | Main API entry point |
| **Service Discovery** | http://localhost:8761 | Eureka dashboard |
| **Admin Dashboard** | http://localhost:9090 | Spring Boot Admin |
| **Config Server** | http://localhost:8888 | Configuration endpoint |
| **API Docs Portal** | http://localhost:8080/docs | Aggregated documentation |

### Individual Service Ports

| Service | Port |
|---------|------|
| Service Discovery | 8761 |
| Config Server | 8888 |
| API Gateway | 8081 |
| Auth Service | 8080 |
| Product Service | 8082 |
| Inventory Service | 8083 |
| Order Service | 8084 |
| Payment Service | 8085 |
| Notification Service | 8086 |
| Cart Service | 8087 |
| Customer Service | 8090 |
| Admin Server | 9090 |
| Webapp | 3000 |

## API Documentation

### Aggregated API Documentation

Visit the API Docs portal at: http://localhost:8080/docs

### Individual Service Documentation

Each service provides its own OpenAPI/Swagger documentation:

- **Auth Service**: http://localhost:8080/api/v1/auth/docs
- **Product Service**: http://localhost:8082/api/v1/product/docs
- **Cart Service**: http://localhost:8087/api/v1/cart/docs
- **Order Service**: http://localhost:8084/api/v1/order/docs

All documentation is also available through the API Gateway with the `/docs` path.

## Authentication & Authorization

### JWT-Based Authentication

The platform uses JSON Web Tokens (JWT) for authentication:

1. **Signup**: POST `/api/v1/auth/sign-up`
2. **Signin**: POST `/api/v1/auth/sign-in` (returns access token)
3. **Token Refresh**: POST `/api/v1/auth/refresh`
4. **Profile**: GET `/api/v1/auth/profile?username={username}`

### Token Usage

Include the JWT in the Authorization header:

```http
Authorization: Bearer <access_token>
```

### RSA Key Pair

JWT tokens are signed using RSA keys:
- Private key: `auth-service/src/main/resources/certs/private.pem`
- Public key: `auth-service/src/main/resources/certs/public.pem`

## Payments / External Integrations

### Payment Flow

1. User creates order → Order Service
2. Order Service initiates payment → Payment Service
3. Payment Service integrates with external payment provider (e.g., Paystack)
4. User completes payment on checkout page
5. Payment Service receives callback → Order Service
6. Order Service updates order status → Notification Service

### Supported Payment Providers

The payment service is designed to support multiple providers. Currently configured for:
- Paystack (Nigeria)

## Error Handling

### Standard Error Response Format

All services return errors in a consistent format:

```json
{
  "code": "ERROR_CODE",
  "message": "User-friendly error message",
  "details": "Additional error details"
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `INTERNAL_ERROR` | 500 | Internal server error |

### Circuit Breaking

The API Gateway implements circuit breaking to prevent cascading failures:
- Timeout: 5 seconds per request
- Retry attempts: 3 (with exponential backoff)
- Fallback responses for unavailable services

## Security Considerations

### Authentication

- JWT tokens with RSA signing
- Token expiration and refresh mechanism
- Secure token storage (client-side localStorage)
- Email verification for signup

### Authorization

- Role-based access control (RBAC)
- Service-to-service authentication
- OAuth2 resource server for API protection

### Data Security

- Encrypted passwords (BCrypt)
- HTTPS recommended for production
- Sensitive data not logged
- SQL injection prevention via JPA

### CORS Configuration

The API Gateway handles CORS for cross-origin requests from the frontend.

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific service
cd auth-service && mvn test

# Skip tests during build
mvn clean install -DskipTests
```

### Test Coverage

Services use:
- **JUnit 5** for unit tests
- **Mockito** for mocking
- **Testcontainers** for integration tests (optional)
- **SpringBootTest** for Spring context testing

## Deployment

### Docker Deployment

```bash
# Build all Docker images
make docker-build

# Start all services
make docker-up

# View logs
make docker-logs

# Stop services
make docker-down
```

### Production Considerations

1. **Environment Variables**: Externalize all configuration
2. **Database**: Use managed PostgreSQL (e.g., AWS RDS)
3. **Redis**: Use managed Redis (e.g., AWS ElastiCache)
4. **Message Queue**: Use managed RabbitMQ (e.g., AWS MQ)
5. **Monitoring**: Enable Zipkin for production tracing
6. **Load Balancing**: Deploy multiple instances behind a load balancer
7. **Secrets Management**: Use Vault or AWS Secrets Manager
8. **CI/CD**: Set up automated builds and deployments

### Kubernetes Deployment

The application can be deployed to Kubernetes using Helm charts or Kubernetes manifests (not included in this repository).

## Future Improvements

### Backend
- [ ] Implement saga pattern for distributed transactions
- [ ] Add search service with Elasticsearch
- [ ] Implement caching strategy for product catalog
- [ ] Add rate limiting at API gateway
- [ ] Implement API versioning strategy
- [ ] Add GraphQL gateway
- [ ] Implement event sourcing for orders

### Frontend
- [ ] Add product image upload
- [ ] Implement wishlists
- [ ] Add product reviews and ratings
- [ ] Implement advanced search/filters
- [ ] Add admin dashboard
- [ ] Implement analytics dashboard
- [ ] Add multi-language support (i18n)

### DevOps
- [ ] Set up CI/CD pipeline (GitHub Actions)
- [ ] Add automated security scanning
- [ ] Implement blue-green deployment
- [ ] Add performance monitoring (Prometheus/Grafana)
- [ ] Set up log aggregation (ELK Stack)
- [ ] Add database migration scripts

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Write unit tests for new functionality
- Update documentation for API changes
- Follow existing code style

## Troubleshooting

### Services Won't Start

1. Check Java version: `java -version` (should be 17+)
2. Check Maven: `mvn -version` (should be 3.8+)
3. Verify ports are available: `lsof -i :8081`
4. Check logs: `tail -f logs/<service>.log`

### Service Discovery Issues

1. Ensure Eureka starts first (port 8761)
2. Check Eureka dashboard: http://localhost:8761
3. Verify service registration in logs

### Database Connection Errors

1. Verify PostgreSQL is running: `pg_isready`
2. Check database credentials in `application.yml`
3. Ensure database exists: `createdb ecommerce-ms`

### Build Errors

```bash
# Clean and rebuild
mvn clean install -U -DskipTests

# Force update dependencies
mvn clean package -U
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Additional Documentation

For detailed information about each service, see the individual README files:

- [Auth Service](./auth-service/README.md)
- [Product Service](./product-service/README.md)
- [Order Service](./order-service/README.md)
- [Cart Service](./cart-service/README.md)
- [Customer Service](./customer-service/README.md)
- [Inventory Service](./inventory-service/README.md)
- [Payment Service](./payment-service/README.md)
- [Notification Service](./notification-service/README.md)
- [API Gateway](./api-gateway/README.md)
- [Service Discovery](./service-discovery/README.md)
- [Config Server](./config-server/README.md)
- [Admin Server](./admin-server/README.md)
- [Webapp](./webapp/README.md)

---

**Happy Coding! 🚀**
