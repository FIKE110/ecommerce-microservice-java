# Auth Service

Authentication and authorization microservice managing user credentials, JWT tokens, and email verification.

## Overview

The Auth Service handles all authentication-related operations including user registration, login, token management, and email verification. It uses JWT (JSON Web Tokens) with RSA encryption for secure authentication.

## Features

- User registration and email verification
- JWT token generation and validation
- Token refresh mechanism
- Password-based authentication
- User profile retrieval
- RSA-based token signing
- Email verification OTP generation

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **Security**: Spring Security + OAuth2 Resource Server
- **Token**: JWT with RSA encryption
- **Service Discovery**: Netflix Eureka
- **Tracing**: Zipkin + Brave
- **Documentation**: SpringDoc OpenAPI

## API Documentation

**Swagger UI**: http://localhost:8080/api/v1/auth/docs
**OpenAPI JSON**: http://localhost:8080/api/v1/auth/v3/api-docs

## Configuration

### Application Properties

```yaml
api-gateway-url: http://localhost:8081
spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

api:
  url: '/api/v1/auth'

rsa:
  private-key: classpath:certs/private.pem
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
| `RSA_PRIVATE_KEY` | Path to RSA private key | `classpath:certs/private.pem` |
| `RSA_PUBLIC_KEY` | Path to RSA public key | `classpath:certs/public.pem` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |

## API Endpoints

### Authentication Endpoints

#### Sign Up
Create a new user account.

```http
POST /api/v1/auth/sign-up
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response**: `201 CREATED`

```json
{
  "code": "SIGNUP_SUCCESSFUL",
  "data": {
    "message": "Signup successful"
  }
}
```

#### Sign In
Authenticate and receive JWT tokens.

```http
POST /api/v1/auth/sign-in
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securePassword123"
}
```

**Response**: `200 OK`

```json
{
  "code": "SIGNIN_SUCCESSFUL",
  "data": {
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 3600,
    "token_type": "Bearer"
  }
}
```

#### Refresh Token
Refresh an expired access token.

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**: `200 OK`

```json
{
  "code": "TOKEN_REFRESHED",
  "data": {
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 3600,
    "token_type": "Bearer"
  }
}
```

#### Send OTP
Send verification OTP to user's email.

```http
POST /api/v1/auth/send-otp
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Response**: `200 OK`

```json
{
  "code": "VERIFICATION_OTP_SENT",
  "data": {
    "message": "Verification OTP sent"
  }
}
```

#### Get Profile
Retrieve user profile by username.

```http
GET /api/v1/auth/profile?username=johndoe
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "verified": true,
  "created_at": "2024-01-01T00:00:00Z"
}
```

#### Forgot Password
Initiate password reset flow.

```http
POST /api/v1/auth/forgot-password
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

## Data Model

### Customer Entity

```java
@Entity
public class Customer {
    @Id
    private UUID id;
    private String username;
    private String email;
    private String password;  // Encrypted
    private Boolean verified;
    private LocalDateTime createdAt;
}
```

### CustomerToken Entity

```java
@Entity
public class CustomerToken {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private TokenType tokenType;
    private LocalDateTime expiryDate;
    @ManyToOne
    private Customer customer;
}
```

## JWT Token Structure

### Access Token
- **Purpose**: Authenticate API requests
- **Expiration**: 1 hour (configurable)
- **Claims**:
  - `sub`: Customer ID
  - `username`: Username
  - `email`: Email address
  - `exp`: Expiration timestamp
  - `iat`: Issued at timestamp

### Refresh Token
- **Purpose**: Obtain new access tokens
- **Expiration**: 7 days (configurable)
- **Claims**:
  - `sub`: Customer ID
  - `type`: "refresh"
  - `exp`: Expiration timestamp

## Security

### Password Encryption

Passwords are encrypted using BCrypt:
- Algorithm: BCrypt
- Strength factor: 10
- One-way encryption (cannot be decrypted)

### RSA Key Pair

JWT tokens are signed using RSA 2048-bit keys:
- **Private Key**: Used to sign tokens (auth-service only)
- **Public Key**: Used to verify tokens (all services)

Key files location:
- `src/main/resources/certs/private.pem`
- `src/main/resources/certs/public.pem`

### Token Validation

All other services validate tokens using the public key:
1. Extract token from `Authorization` header
2. Verify signature using public key
3. Check expiration
4. Extract user claims

## Service Interactions

The Auth Service communicates with:

- **Customer Service**: Creates customer profile on signup
- **Notification Service**: Sends verification emails
- **All Services**: Provides public key for token validation

## Error Handling

### Error Response Format

```json
{
  "code": "ERROR_CODE",
  "data": {
    "message": "User-friendly error message"
  }
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_CREDENTIALS` | 401 | Username or password incorrect |
| `USER_ALREADY_EXISTS` | 409 | Email or username already registered |
| `TOKEN_EXPIRED` | 401 | JWT token has expired |
| `INVALID_TOKEN` | 401 | JWT token is invalid |
| `USER_NOT_FOUND` | 404 | User does not exist |
| `VERIFICATION_FAILED` | 400 | OTP verification failed |

## Running the Service

### Local Development

```bash
cd auth-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd auth-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t auth-service:latest .

# Run container
docker run -p 8080:8080 auth-service:latest
```

## Testing

### Run Tests

```bash
cd auth-service
mvn test
```

### Test Coverage

- Unit tests for AuthService
- Integration tests for AuthController
- Token generation and validation tests
- Authentication flow tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Deployment Notes

### Production Configuration

1. **Secrets Management**: Store RSA keys in a secure vault
2. **Database**: Use managed PostgreSQL instance
3. **Environment**: Set `spring.profiles.active=production`
4. **Logging**: Configure structured logging (JSON format)
5. **Monitoring**: Enable health checks and metrics

### Scaling

- Deploy multiple instances behind load balancer
- Use sticky sessions if needed (though JWT is stateless)
- Consider read replicas for database

## Troubleshooting

### Token Verification Failures

1. Check RSA public key is accessible
2. Verify token hasn't expired
3. Check Eureka for service registration
4. Review logs for detailed error messages

### Database Connection Issues

1. Verify PostgreSQL is running
2. Check connection string in application.yml
3. Ensure database user has proper permissions
4. Test connection: `psql -h localhost -U postgres -d ecommerce-ms`

### Service Registration Issues

1. Ensure Eureka server is running
2. Check Eureka URL configuration
3. Verify network connectivity
4. Review service instance ID in logs

## Future Improvements

- [ ] Implement 2FA (Two-Factor Authentication)
- [ ] Add social login (Google, GitHub, etc.)
- [ ] Implement session management
- [ ] Add rate limiting for authentication endpoints
- [ ] Implement password history
- [ ] Add account lockout after failed attempts
- [ ] Support for multi-factor authentication

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

For more information, see the [main project README](../README.md).
