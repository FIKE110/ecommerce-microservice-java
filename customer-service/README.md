# Customer Service

Customer profile management microservice handling user account information and preferences.

## Overview

The Customer Service manages customer profiles including personal information, addresses, preferences, and account settings. It provides RESTful APIs for customer management and integrates with Auth Service for user authentication.

## Features

- Customer profile creation and management
- Personal information updates
- Address management
- Customer preferences
- Account status management
- Profile validation
- Integration with Auth Service

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + OAuth2 Resource Server
- **Validation**: Jakarta Bean Validation
- **Service Discovery**: Netflix Eureka
- **Tracing**: Zipkin + Brave

## API Documentation

**Swagger UI**: http://localhost:8090/api/v1/customer/docs
**OpenAPI JSON**: http://localhost:8090/api/v1/customer/v3/api-docs

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: customer-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

api:
  url: '/api/v1/customer'

rsa:
  public-key: classpath:certs/public.pem

server:
  port: 0  # Random port assigned by Eureka
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/ecommercems` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` |
| `RSA_PUBLIC_KEY` | Path to RSA public key | `classpath:certs/public.pem` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |

## API Endpoints

### Customer Profile Endpoints

#### Get Customer Profile
Retrieve customer profile by username.

```http
GET /api/v1/customer/profile?username=johndoe
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1-555-1234",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "preferences": {
    "language": "en",
    "currency": "USD"
  },
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-15T00:00:00Z"
}
```

#### Create Customer Profile
Create a new customer profile.

```http
POST /api/v1/customer/profile
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1-555-1234",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "preferences": {
    "language": "en",
    "currency": "USD"
  }
}
```

**Response**: `201 CREATED`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "message": "Customer profile created successfully"
}
```

#### Update Customer Profile
Update customer profile information.

```http
PUT /api/v1/customer/profile/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "firstName": "Johnathan",
  "lastName": "Doe",
  "phone": "+1-555-5678"
}
```

**Response**: `200 OK`

```json
{
  "message": "Customer profile updated successfully"
}
```

#### Update Address
Update customer shipping address.

```http
PUT /api/v1/customer/profile/{id}/address
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "street": "456 Oak Ave",
  "city": "Los Angeles",
  "state": "CA",
  "zipCode": "90001",
  "country": "USA"
}
```

**Response**: `200 OK`

```json
{
  "message": "Address updated successfully"
}
```

#### Update Preferences
Update customer preferences.

```http
PUT /api/v1/customer/profile/{id}/preferences
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "language": "es",
  "currency": "EUR"
}
```

**Response**: `200 OK`

```json
{
  "message": "Preferences updated successfully"
}
```

#### Delete Customer Profile
Delete customer profile.

```http
DELETE /api/v1/customer/profile/{id}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "message": "Customer profile deleted successfully"
}
```

## Data Model

### CustomerProfile Entity

```java
@Entity
public class CustomerProfile {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    @Embedded
    private Address address;
    @Embedded
    private Preferences preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Address Embeddable

```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
```

### Preferences Embeddable

```java
@Embeddable
public class Preferences {
    private String language;
    private String currency;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
}
```

## Service Interactions

The Customer Service communicates with:

- **Auth Service**: Validates user tokens and syncs user data
- **Order Service**: Provides customer information for orders
- **Notification Service**: Sends notifications for profile updates
- **Cart Service**: Uses customer preferences for cart display

## Error Handling

### Error Response Format

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "User-friendly error message",
    "details": "Additional information"
  }
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `CUSTOMER_NOT_FOUND` | 404 | Customer profile does not exist |
| `USERNAME_EXISTS` | 409 | Username already exists |
| `EMAIL_EXISTS` | 409 | Email already exists |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |
| `FORBIDDEN` | 403 | Insufficient permissions |

## Validation Rules

### Create/Update Profile

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `username` | String | Yes | 3-50 characters, alphanumeric |
| `email` | String | Yes | Valid email format |
| `firstName` | String | Yes | 1-50 characters |
| `lastName` | String | Yes | 1-50 characters |
| `phone` | String | No | Valid phone number format |
| `address` | Object | No | Valid address object |

## Running Service

### Local Development

```bash
cd customer-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd customer-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t customer-service:latest .

# Run container
docker run -p 8090:8090 customer-service:latest
```

## Testing

### Run Tests

```bash
cd customer-service
mvn test
```

### Test Coverage

- Unit tests for CustomerService
- Integration tests for CustomerController
- Profile validation tests
- Address management tests
- Preferences tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8090/actuator/health
- **Metrics**: http://localhost:8090/actuator/metrics
- **Info**: http://localhost:8090/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Deployment Notes

### Production Configuration

1. **Database Connection Pool**: Configure HikariCP
2. **Connection Timeout**: Set appropriate timeout values
3. **Data Encryption**: Encrypt sensitive customer data
4. **Backup Strategy**: Regular database backups
5. **GDPR Compliance**: Implement data deletion requests

### Scaling

- Deploy multiple instances behind load balancer
- Use read replicas for database
- Implement caching for frequently accessed profiles
- Consider CDN for static data

## Troubleshooting

### Profile Not Found

1. Verify customer ID format (UUID)
2. Check database for customer profile
3. Review logs for errors
4. Test with API client

### Validation Errors

1. Check request body format
2. Verify field constraints
3. Ensure required fields are present
4. Review validation error messages

### Database Connection Issues

1. Verify PostgreSQL is running
2. Check connection string in application.yml
3. Ensure database user has proper permissions
4. Test connection: `psql -h localhost -U postgres -d ecommerce-ms`

## Future Improvements

- [ ] Implement customer loyalty program
- [ ] Add customer support ticket system
- [ ] Implement customer segmentation
- [ ] Add social media integration
- [ ] Implement profile picture upload
- [ ] Add multiple address management
- [ ] Implement customer feedback system
- [ ] Add data export functionality (GDPR)

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.7</version>
    </dependency>
    <!-- ... -->
</dependencies>
```

---

For more information, see: [main project README](../README.md).
