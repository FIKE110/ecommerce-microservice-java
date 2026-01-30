# Payment Service

Payment processing microservice handling payment transactions, refunds, and integration with payment providers.

## Overview

The Payment Service manages payment processing including payment initiation, callback handling, refunds, and integration with external payment providers (e.g., Paystack, Stripe, PayPal). It provides secure payment APIs for the e-commerce platform.

## Features

- Payment initiation and processing
- Payment callback handling
- Refund processing
- Payment history tracking
- Integration with multiple payment providers
- Transaction logging
- Redis-based session management
- Webhook handling

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **Cache**: Redis
- **ORM**: Spring Data JPA + Hibernate
- **Security**: OAuth2 Resource Server
- **Validation**: Jakarta Bean Validation
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **HTTP Client**: OpenFeign
- **Tracing**: Zipkin + Brave

## API Documentation

**Swagger UI**: http://localhost:8085/api/v1/payment/docs
**OpenAPI JSON**: http://localhost:8085/api/v1/payment/v3/api-docs

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: payment-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672

api:
  url: '/api/v1/payment'

payment:
  provider: paystack  # or stripe, paypal
  paystack:
    secret-key: ${PAYSTACK_SECRET_KEY}
    public-key: ${PAYSTACK_PUBLIC_KEY}
  stripe:
    secret-key: ${STRIPE_SECRET_KEY}
    public-key: ${STRIPE_PUBLIC_KEY}

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
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `RSA_PUBLIC_KEY` | Path to RSA public key | `classpath:certs/public.pem` |
| `PAYMENT_PROVIDER` | Payment provider | `paystack` |
| `PAYSTACK_SECRET_KEY` | Paystack secret key | - |
| `PAYSTACK_PUBLIC_KEY` | Paystack public key | - |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |

## API Endpoints

### Payment Processing Endpoints

#### Initiate Payment
Initialize a payment transaction.

```http
POST /api/v1/payment/initiate
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "orderId": "770e0600-e49b-61d4-b916-666656560000",
  "amount": 379.97,
  "currency": "USD",
  "email": "customer@example.com",
  "callbackUrl": "https://api.example.com/api/v1/payment/callback",
  "metadata": {
    "userId": "user-123",
    "source": "web"
  }
}
```

**Response**: `200 OK`

```json
{
  "reference": "PAY_1234567890",
  "checkoutUrl": "https://checkout.paystack.co/abc123",
  "amount": 37997,
  "currency": "USD",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:00:00Z"
}
```

#### Payment Callback
Handle payment callback from payment provider (webhook).

```http
POST /api/v1/payment/callback
Content-Type: application/json

{
  "reference": "PAY_1234567890",
  "status": "success",
  "amount": 37997,
  "currency": "USD",
  "transaction_date": "2024-01-15T10:05:00Z"
}
```

**Response**: `200 OK`

```json
{
  "status": "confirmed",
  "message": "Payment processed successfully"
}
```

#### Get Payment Status
Check status of a payment.

```http
GET /api/v1/payment/status/{reference}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "reference": "PAY_1234567890",
  "orderId": "770e0600-e49b-61d4-b916-666656560000",
  "amount": 379.97,
  "currency": "USD",
  "status": "SUCCESS",
  "paymentMethod": "card",
  "createdAt": "2024-01-15T10:00:00Z",
  "completedAt": "2024-01-15T10:05:00Z"
}
```

#### Get Payment History
Get payment history for a user.

```http
GET /api/v1/payment/history?username=johndoe&page=0&size=10
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "content": [
    {
      "reference": "PAY_1234567890",
      "orderId": "770e0600-e49b-61d4-b916-666656560000",
      "amount": 379.97,
      "status": "SUCCESS",
      "createdAt": "2024-01-15T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### Get Invoice
Retrieve invoice details for a payment.

```http
GET /api/v1/payment/invoice/{reference}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "invoiceUrl": "https://payment-provider.com/invoice/PAY_1234567890",
  "reference": "PAY_1234567890",
  "amount": 379.97,
  "currency": "USD",
  "status": "PAID",
  "items": [
    {
      "name": "Wireless Headphones",
      "quantity": 2,
      "price": 149.99
    }
  ]
}
```

#### Process Refund
Initiate a refund for a payment.

```http
POST /api/v1/payment/{reference}/refund
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "amount": 100.00,
  "reason": "Product returned"
}
```

**Response**: `200 OK`

```json
{
  "refundId": "REF_9876543210",
  "reference": "PAY_1234567890",
  "amount": 100.00,
  "status": "PROCESSING",
  "message": "Refund initiated successfully"
}
```

## Data Model

### Payment Entity

```java
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;
    private String reference;
    private UUID orderId;
    private String username;
    private Double amount;
    private String currency;
    private PaymentStatus status;
    private String paymentMethod;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Refund> refunds;
}
```

### Refund Entity

```java
@Entity
public class Refund {
    @Id
    @GeneratedValue
    private UUID id;
    private String refundId;
    @ManyToOne
    private Payment payment;
    private Double amount;
    private String reason;
    private RefundStatus status;
    private LocalDateTime createdAt;
}
```

### PaymentStatus Enum

```java
public enum PaymentStatus {
    PENDING,      // Payment initiated
    PROCESSING,   // Payment being processed
    SUCCESS,      // Payment completed successfully
    FAILED,       // Payment failed
    CANCELLED,    // Payment cancelled
    REFUNDED      // Payment refunded
}
```

### RefundStatus Enum

```java
public enum RefundStatus {
    PENDING,       // Refund initiated
    PROCESSING,    // Refund being processed
    SUCCESS,       // Refund completed
    FAILED         // Refund failed
}
```

## Payment Flow

### Payment Initiation Flow

```
1. Order created in Order Service
2. Order Service calls Payment Service to initiate payment
3. Payment Service creates payment record (PENDING status)
4. Payment Service calls payment provider (Paystack)
5. Payment provider returns checkout URL
6. Payment Service returns checkout URL to Order Service
7. User redirected to checkout page
8. User completes payment on payment provider's page
```

### Payment Callback Flow

```
1. User completes payment on payment provider
2. Payment provider sends callback to Payment Service
3. Payment Service verifies callback signature
4. Payment Service updates payment status
5. Payment Service publishes event to RabbitMQ
6. Order Service receives event and updates order status
7. Notification Service sends confirmation email
```

### Refund Flow

```
1. User requests refund
2. Admin approves refund
3. Payment Service initiates refund with provider
4. Payment provider processes refund
5. Payment provider sends callback
6. Payment Service updates refund status
7. Payment Service publishes event to RabbitMQ
8. Order Service updates order status
9. Notification Service sends refund notification
```

## Payment Provider Integration

### Paystack Integration

```java
@Service
public class PaystackPaymentProvider implements PaymentProvider {

    @Value("${payment.paystack.secret-key}")
    private String secretKey;

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
        // Call Paystack API to initiate payment
        // Return checkout URL and reference
    }

    @Override
    public boolean verifyCallback(String reference) {
        // Call Paystack API to verify payment
        // Return payment status
    }
}
```

### Supported Providers

| Provider | Status | Features |
|----------|--------|----------|
| Paystack | ✅ Active | Cards, Bank Transfer |
| Stripe | 🔜 Planned | Cards, Apple Pay, Google Pay |
| PayPal | 🔜 Planned | PayPal Balance, Cards |

## Security

### Webhook Verification

Verify payment provider webhooks to prevent fraud:

```java
@Component
public class WebhookValidator {

    public boolean isValidSignature(String payload, String signature) {
        // Verify HMAC signature
        // Compare with provider's secret key
        return true;
    }
}
```

### PCI Compliance

- Store no sensitive card data
- Use provider's hosted checkout pages
- Encrypt all payment data in transit
- Implement fraud detection
- Regular security audits

## Service Interactions

The Payment Service communicates with:

- **Order Service**: Updates order status based on payment
- **Notification Service**: Sends payment confirmation emails
- **Customer Service**: Validates customer information
- **External Payment Providers**: Processes payments and refunds

## RabbitMQ Integration

### Event Publishing

```java
@RabbitListener(queues = "${rabbitmq.queue.name}")
public void handleOrderEvent(OrderEvent event) {
    switch (event.getType()) {
        case ORDER_CREATED:
            // Payment already initiated by Order Service
            break;
        case ORDER_CANCELLED:
            cancelPayment(event.getPaymentReference());
            break;
    }
}
```

### Event Types

- `PAYMENT_INITIATED`: New payment created
- `PAYMENT_SUCCESS`: Payment completed successfully
- `PAYMENT_FAILED`: Payment failed
- `PAYMENT_REFUNDED`: Payment refunded

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
| `PAYMENT_NOT_FOUND` | 404 | Payment with specified reference does not exist |
| `INVALID_AMOUNT` | 400 | Invalid payment amount |
| `PAYMENT_FAILED` | 400 | Payment processing failed |
| `REFUND_FAILED` | 400 | Refund processing failed |
| `INVALID_WEBHOOK` | 400 | Invalid webhook signature |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |

## Validation Rules

### Initiate Payment

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `orderId` | UUID | Yes | Valid UUID |
| `amount` | Double | Yes | Must be positive |
| `currency` | String | Yes | ISO currency code (e.g., USD) |
| `email` | String | Yes | Valid email address |

## Running Service

### Local Development

```bash
cd payment-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd payment-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t payment-service:latest .

# Run container
docker run -p 8085:8085 payment-service:latest
```

### Redis Setup

```bash
# Using Docker
docker run -d -p 6379:6379 redis:latest

# Or using Docker Compose
docker-compose up -d redis
```

## Testing

### Run Tests

```bash
cd payment-service
mvn test
```

### Test Coverage

- Unit tests for PaymentService
- Integration tests for PaymentController
- Payment provider integration tests
- Refund processing tests
- Webhook verification tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8085/actuator/health
- **Metrics**: http://localhost:8085/actuator/metrics
- **Info**: http://localhost:8085/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Security Best Practices

1. **Never log sensitive data**: Card numbers, CVV, etc.
2. **Use HTTPS**: Always use TLS for payment endpoints
3. **Verify webhooks**: Always validate webhook signatures
4. **Implement fraud detection**: Check for suspicious activity
5. **Follow PCI DSS**: Compliance with Payment Card Industry Data Security Standard
6. **Rotate API keys**: Regularly rotate payment provider keys

## Deployment Considerations

### Production Configuration

1. **Payment Provider Keys**: Store in secure vault (not in code)
2. **Database Connection Pool**: Configure HikariCP
3. **Connection Timeout**: Set appropriate timeout values
4. **Webhook Security**: Use HTTPS and signature verification
5. **Monitoring**: Set up alerts for payment failures

### Scaling

- Deploy multiple instances behind load balancer
- Use Redis for distributed session management
- Implement payment queue for high-volume processing
- Consider regional deployment for reduced latency

## Troubleshooting

### Payment Not Processing

1. Verify payment provider credentials
2. Check payment provider status page
3. Review payment service logs
4. Test with payment provider's test mode
5. Check webhook URL accessibility

### Callback Not Received

1. Verify webhook URL is publicly accessible
2. Check firewall settings
3. Review payment provider webhook logs
4. Test webhook manually with provider's tools

### Refund Issues

1. Verify refund time limits (usually 30-180 days)
2. Check payment status (must be SUCCESS)
3. Review refund reason codes
4. Test with payment provider's dashboard

## Future Improvements

- [ ] Implement Stripe integration
- [ ] Add PayPal support
- [ ] Implement subscription payments
- [ ] Add payment analytics
- [ ] Implement fraud detection
- [ ] Add multi-currency support
- [ ] Implement payment dispute handling
- [ ] Add payment method management

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
        <artifactId>spring-boot-starter-data-redis</artifactId>
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
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
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
