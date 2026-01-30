# Order Service

Order processing microservice managing order creation, status tracking, payment integration, and order history.

## Overview

The Order Service handles the complete order lifecycle including order creation, payment processing, status updates, and order history retrieval. It integrates with Payment Service for payment processing and publishes events to other services via RabbitMQ.

## Features

- Order creation with multiple products
- Order status tracking (pending, confirmed, shipped, delivered, cancelled)
- Order history retrieval by user
- Payment integration with external providers
- Invoice generation
- Order reordering
- Integration with Payment Service
- Event-driven communication via RabbitMQ

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **HTTP Client**: OpenFeign
- **Tracing**: Zipkin + Brave
- **Validation**: Jakarta Bean Validation

## API Documentation

**Swagger UI**: http://localhost:8084/api/v1/order/docs
**OpenAPI JSON**: http://localhost:8084/api/v1/order/v3/api-docs

## Configuration

### Application Properties

```yaml
api-gateway-url: http://localhost:8081
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

api:
  url: '/api/v1/order'

server:
  port: 0  # Random port assigned by Eureka
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/ecommercems` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `SPRING_RABBITMQ_HOST` | RabbitMQ host | `localhost` |
| `SPRING_RABBITMQ_PORT` | RabbitMQ port | `5672` |

## API Endpoints

### Order Management Endpoints

#### Create Order
Create a new order with multiple products.

```http
POST /api/v1/order?username=johndoe
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "products": {
    "550e8400-e29b-41d4-a716-446655440000": 2,
    "660e9500-e39b-51d4-a816-556655550000": 1
  }
}
```

**Response**: `200 OK`

```json
{
  "checkout_url": "https://checkout.payment-provider.com/pay/abc123"
}
```

**Order Flow**:
1. Order is created with "PENDING" status
2. Payment is initiated via Payment Service
3. Returns checkout URL for user to complete payment
4. On payment success, order status updates to "CONFIRMED"

#### Get Orders
Get paginated list of all orders.

```http
GET /api/v1/order?page=0&size=10&sort=createdAt
```

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 1)
- `sort` (optional): Sort field (default: createdAt)

**Response**: `200 OK`

```json
{
  "content": [
    {
      "id": "770e0600-e49b-61d4-b916-666656560000",
      "username": "johndoe",
      "status": "CONFIRMED",
      "totalAmount": 379.97,
      "paymentReference": "PAY123456",
      "paymentLink": "https://checkout.payment-provider.com/pay/abc123",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:05:00Z",
      "items": [
        {
          "productId": "550e8400-e29b-41d4-a716-446655440000",
          "quantity": 2,
          "price": 149.99
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 1,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### Get Order by ID
Retrieve specific order details.

```http
GET /api/v1/order/{id}
```

**Response**: `200 OK`

```json
{
  "id": "770e0600-e49b-61d4-b916-666656560000",
  "username": "johndoe",
  "status": "CONFIRMED",
  "totalAmount": 379.97,
  "paymentReference": "PAY123456",
  "paymentLink": "https://checkout.payment-provider.com/pay/abc123",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:05:00Z",
  "items": [
    {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "price": 149.99
    },
    {
      "productId": "660e9500-e39b-51d4-a816-556655550000",
      "quantity": 1,
      "price": 79.99
    }
  ]
}
```

#### Get Orders by Username
Get paginated list of orders for a specific user.

```http
GET /api/v1/order/username?username=johndoe&page=0&size=10&sort=createdAt
```

**Response**: `200 OK` (same format as Get Orders)

#### Get Invoice
Retrieve invoice details for an order.

```http
GET /api/v1/order/invoice/{reference}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "invoiceUrl": "https://payment-provider.com/invoice/PAY123456",
  "reference": "PAY123456",
  "amount": 379.97,
  "currency": "USD",
  "status": "PAID"
}
```

#### Reorder Order
Recreate an existing order (for reordering).

```http
POST /api/v1/order/reorder/{id}?username=johndoe
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "checkout_url": "https://checkout.payment-provider.com/pay/xyz789"
}
```

## Data Model

### Order Entity

```java
@Entity
public class Order {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private OrderStatus status;
    private Double totalAmount;
    private String paymentReference;
    private String paymentLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
```

### OrderItem Entity

```java
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID productId;
    private Long quantity;
    private Double price;

    @ManyToOne
    private Order order;
}
```

### OrderStatus Enum

```java
public enum OrderStatus {
    PENDING,      // Order created, awaiting payment
    CONFIRMED,    // Payment successful
    PROCESSING,   // Order being prepared
    SHIPPED,      // Order shipped
    DELIVERED,    // Order delivered
    CANCELLED     // Order cancelled
}
```

## Order Lifecycle

### Order Status Flow

```
PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
           ↓
        CANCELLED
```

### State Transitions

| From | To | Trigger |
|------|-----|--------|
| PENDING | CONFIRMED | Payment successful |
| PENDING | CANCELLED | Payment failed/timeout |
| CONFIRMED | PROCESSING | Order processing started |
| PROCESSING | SHIPPED | Order shipped |
| SHIPPED | DELIVERED | Order delivered |
| Any | CANCELLED | User/admin cancellation |

## Payment Integration

### Payment Flow

1. **Order Creation**: Order created with PENDING status
2. **Payment Initiation**: Order Service calls Payment Service
3. **Checkout URL**: Payment Service returns checkout URL
4. **User Payment**: User completes payment on external page
5. **Payment Callback**: Payment Service receives callback
6. **Order Update**: Order status updated to CONFIRMED
7. **Notification**: Notification Service sends confirmation email

### PaymentClient (Feign)

```java
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentClient {
    @PostMapping("/api/v1/payment/initiate")
    String initiatePayment(
        @RequestHeader("Authorization") String token,
        @RequestBody Map<String, Object> params
    );

    @GetMapping("/api/v1/payment/invoice/{reference}")
    Map<String, String> getInvoice(
        @RequestHeader("Authorization") String token,
        @PathVariable("reference") String reference
    );
}
```

## Service Interactions

The Order Service communicates with:

- **Payment Service**: Initiates payments and retrieves invoices
- **Product Service**: Validates product existence and pricing
- **Inventory Service**: Checks and reserves stock
- **Customer Service**: Validates customer information
- **Cart Service**: Clears cart after order creation
- **Notification Service**: Sends order confirmation emails

## Event Publishing

### RabbitMQ Events

The Order Service publishes events to RabbitMQ:

```java
@RabbitListener(queues = "${rabbitmq.queue.name}")
public void handleOrderEvent(OrderEvent event) {
    // Handle order events
}
```

### Event Types

- `ORDER_CREATED`: New order created
- `ORDER_CONFIRMED`: Payment successful
- `ORDER_CANCELLED`: Order cancelled
- `ORDER_SHIPPED`: Order shipped
- `ORDER_DELIVERED`: Order delivered

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
| `ORDER_NOT_FOUND` | 404 | Order with specified ID does not exist |
| `INVALID_ORDER_STATUS` | 400 | Invalid order status transition |
| `PAYMENT_FAILED` | 400 | Payment processing failed |
| `INSUFFICIENT_STOCK` | 400 | Not enough stock available |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |

## Validation Rules

### Create Order

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `username` | String | Yes | Must exist in Customer Service |
| `products` | Map | Yes | At least one product |
| `products.{id}` | UUID | Yes | Valid product ID |
| `products.{quantity}` | Long | Yes | Must be positive |

## Running the Service

### Local Development

```bash
cd order-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd order-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t order-service:latest .

# Run container
docker run -p 8084:8084 order-service:latest
```

## Testing

### Run Tests

```bash
cd order-service
mvn test
```

### Test Coverage

- Unit tests for OrderService
- Integration tests for OrderController
- Payment integration tests
- Order lifecycle tests
- Event publishing tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8084/actuator/health
- **Metrics**: http://localhost:8084/actuator/metrics
- **Info**: http://localhost:8084/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Performance Considerations

### Database Optimization

- Index on `username` for user orders
- Index on `createdAt` for sorting
- Index on `status` for filtering

### Idempotency

Order creation should be idempotent to handle duplicate requests:
- Generate unique order ID upfront
- Check for existing order before creation
- Return existing order if duplicate detected

### Transaction Management

Use database transactions for:
- Order creation with items
- Status updates
- Payment processing

## Deployment Notes

### Production Configuration

1. **Database Connection Pool**: Configure HikariCP
2. **Timeout Settings**: Set appropriate timeout values
3. **Retry Logic**: Implement retry for payment calls
4. **Circuit Breaker**: Use resilience4j for payment service calls
5. **Async Processing**: Consider async payment callback handling

### Scaling

- Deploy multiple instances behind load balancer
- Use database read replicas for order queries
- Implement order queue for high-volume processing
- Consider sharding by customer ID for large scale

## Troubleshooting

### Order Not Created

1. Check product existence in Product Service
2. Verify stock availability in Inventory Service
3. Review logs for validation errors
4. Test with API client

### Payment Integration Issues

1. Check Payment Service availability
2. Verify payment credentials
3. Review payment callback logs
4. Test payment flow manually

### Status Update Failures

1. Check RabbitMQ connection
2. Verify event publishing configuration
3. Review consumer logs
4. Test event flow manually

## Future Improvements

- [ ] Implement order cancellation policy
- [ ] Add order tracking with shipment updates
- [ ] Implement order returns and refunds
- [ ] Add order analytics dashboard
- [ ] Implement order priority system
- [ ] Add bulk order operations
- [ ] Implement order versioning
- [ ] Add order export functionality

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
    <!-- ... -->
</dependencies>
```

---

For more information, see: [main project README](../README.md).
