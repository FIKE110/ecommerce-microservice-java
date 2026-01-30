# Inventory Service

Inventory management microservice handling product stock, reservations, and availability tracking.

## Overview

The Inventory Service manages product inventory including stock levels, reservations, availability checks, and stock updates. It provides APIs for inventory management and integrates with Product Service and Order Service.

## Features

- Product stock management
- Inventory availability checks
- Stock reservation for orders
- Stock updates and adjustments
- Low stock alerts
- Inventory reporting
- Integration with RabbitMQ for event-driven updates

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Security**: OAuth2 Resource Server
- **Validation**: Jakarta Bean Validation
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **Tracing**: Zipkin + Brave

## API Documentation

**Swagger UI**: http://localhost:8083/api/v1/inventory/docs
**OpenAPI JSON**: http://localhost:8083/api/v1/inventory/v3/api-docs

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: inventory-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  rabbitmq:
    host: localhost
    port: 5672

api:
  url: '/api/v1/inventory'

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
| `SPRING_RABBITMQ_HOST` | RabbitMQ host | `localhost` |
| `SPRING_RABBITMQ_PORT` | RabbitMQ port | `5672` |

## API Endpoints

### Inventory Management Endpoints

#### Get Inventory by Product ID
Retrieve inventory information for a specific product.

```http
GET /api/v1/inventory/product/{productId}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "productId": "550e8400-e29b-41d4-a716-446655440001",
  "quantity": 100,
  "reserved": 5,
  "available": 95,
  "lowStockThreshold": 10,
  "lastUpdated": "2024-01-15T10:30:00Z"
}
```

#### Check Availability
Check if a product is available in required quantity.

```http
GET /api/v1/inventory/product/{productId}/availability?quantity=10
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "productId": "550e8400-e29b-41d4-a716-446655440001",
  "available": true,
  "availableQuantity": 95,
  "requestedQuantity": 10
}
```

#### Reserve Stock
Reserve stock for an order (temporary hold).

```http
POST /api/v1/inventory/product/{productId}/reserve
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "quantity": 10,
  "orderId": "770e0600-e49b-61d4-b916-666656560000",
  "reservationMinutes": 30
}
```

**Response**: `200 OK`

```json
{
  "reservationId": "880e1700-e59b-71d4-c016-776667670000",
  "productId": "550e8400-e29b-41d4-a716-4466554401",
  "quantity": 10,
  "reservedUntil": "2024-01-15T11:00:00Z",
  "message": "Stock reserved successfully"
}
```

#### Confirm Reservation
Confirm a reservation (convert reserved stock to actual deduction).

```http
POST /api/v1/inventory/reservation/{reservationId}/confirm
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "message": "Reservation confirmed, stock deducted"
}
```

#### Cancel Reservation
Cancel a reservation (release reserved stock).

```http
POST /api/v1/inventory/reservation/{reservationId}/cancel
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "message": "Reservation cancelled, stock released"
}
```

#### Add Stock
Add stock to a product (admin operation).

```http
POST /api/v1/inventory/product/{productId}/add
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "quantity": 50,
  "reason": "New shipment"
}
```

**Response**: `200 OK`

```json
{
  "message": "Stock added successfully",
  "newQuantity": 150
}
```

#### Reduce Stock
Reduce stock from a product (for returns, damages, etc.).

```http
POST /api/v1/inventory/product/{productId}/reduce
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "quantity": 5,
  "reason": "Damaged goods"
}
```

**Response**: `200 OK`

```json
{
  "message": "Stock reduced successfully",
  "newQuantity": 95
}
```

#### Update Low Stock Threshold
Set low stock alert threshold for a product.

```http
PUT /api/v1/inventory/product/{productId}/threshold
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "threshold": 20
}
```

**Response**: `200 OK`

```json
{
  "message": "Low stock threshold updated",
  "threshold": 20
}
```

#### Get All Inventory
Get inventory for multiple products (batch operation).

```http
POST /api/v1/inventory/products/batch
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "productIds": [
    "550e8400-e29b-41d4-a716-4466554401",
    "660e9500-e39b-51d4-a816-556655550001"
  ]
}
```

**Response**: `200 OK`

```json
{
  "inventories": [
    {
      "productId": "550e8400-e29b-41d4-a716-4466554401",
      "available": 95,
      "lowStock": false
    },
    {
      "productId": "660e9500-e39b-51d4-a816-556655550001",
      "available": 5,
      "lowStock": true
    }
  ]
}
```

## Data Model

### Inventory Entity

```java
@Entity
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID productId;
    private Long quantity;
    private Long reserved;
    private Long available;
    private Long lowStockThreshold;
    private LocalDateTime lastUpdated;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
```

### Reservation Entity

```java
@Entity
public class Reservation {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID productId;
    private UUID orderId;
    private Long quantity;
    private LocalDateTime reservedAt;
    private LocalDateTime reservedUntil;
    private ReservationStatus status;
}
```

### ReservationStatus Enum

```java
public enum ReservationStatus {
    PENDING,     // Reservation created, awaiting confirmation
    CONFIRMED,   // Reservation confirmed, stock deducted
    CANCELLED,    // Reservation cancelled, stock released
    EXPIRED       // Reservation expired
}
```

## Inventory Flow

### Stock Reservation Process

```
1. Order created in Order Service
2. Order Service calls Inventory Service to reserve stock
3. Inventory Service creates reservation
4. Stock is reserved (not deducted)
5. Payment processing initiated
6. On payment success:
   - Reservation confirmed
   - Stock deducted
   - Order status updated
7. On payment failure:
   - Reservation cancelled
   - Stock released
```

### Stock Update Process

```
1. New stock received
2. Admin adds stock via API
3. Inventory Service updates quantity
4. Publishes event to RabbitMQ
5. Product Service updates availability
6. Notification Service alerts if stock was low
```

## RabbitMQ Integration

### Event Publishing

```java
@RabbitListener(queues = "${rabbitmq.queue.name}")
public void handleOrderEvent(OrderEvent event) {
    switch (event.getType()) {
        case ORDER_CREATED:
            reserveStock(event);
            break;
        case ORDER_CONFIRMED:
            confirmReservation(event.getReservationId());
            break;
        case ORDER_CANCELLED:
            cancelReservation(event.getReservationId());
            break;
    }
}
```

### Event Types

- `STOCK_RESERVED`: Stock reserved for order
- `STOCK_CONFIRMED`: Reservation confirmed, stock deducted
- `STOCK_RELEASED`: Reservation cancelled, stock released
- `STOCK_UPDATED`: Stock quantity updated
- `LOW_STOCK`: Low stock alert

## Service Interactions

The Inventory Service communicates with:

- **Product Service**: Receives product updates, syncs stock availability
- **Order Service**: Handles stock reservations and confirmations
- **Notification Service**: Sends low stock alerts
- **Auth Service**: Validates JWT tokens

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
| `PRODUCT_NOT_FOUND` | 404 | Product does not exist in inventory |
| `INSUFFICIENT_STOCK` | 400 | Not enough stock available |
| `RESERVATION_NOT_FOUND` | 404 | Reservation does not exist |
| `RESERVATION_EXPIRED` | 400 | Reservation has expired |
| `INVALID_QUANTITY` | 400 | Invalid quantity specified |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |

## Validation Rules

### Reserve Stock

| Field | Type | Constraints |
|-------|------|-------------|
| `quantity` | Long | Must be positive |
| `orderId` | UUID | Must be valid UUID |
| `reservationMinutes` | Integer | Must be positive (default: 30) |

### Add/Reduce Stock

| Field | Type | Constraints |
|-------|------|-------------|
| `quantity` | Long | Must be positive |
| `reason` | String | Required for audit trail |

## Running the Service

### Local Development

```bash
cd inventory-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd inventory-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t inventory-service:latest .

# Run container
docker run -p 8083:8083 inventory-service:latest
```

### RabbitMQ Setup

```bash
# Using Docker
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:management

# Or using Docker Compose
docker-compose up -d rabbitmq
```

## Testing

### Run Tests

```bash
cd inventory-service
mvn test
```

### Test Coverage

- Unit tests for InventoryService
- Integration tests for InventoryController
- Reservation flow tests
- Stock management tests
- RabbitMQ integration tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8083/actuator/health
- **Metrics**: http://localhost:8083/actuator/metrics
- **Info**: http://localhost:8083/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

### RabbitMQ Monitoring

```bash
# Access RabbitMQ Management UI
http://localhost:15672

# Default credentials
Username: guest
Password: guest
```

## Performance Considerations

### Database Optimization

- Index on `productId` for fast lookups
- Index on `reservedUntil` for reservation expiry
- Use connection pooling (HikariCP)
- Optimize queries for batch operations

### Caching Strategy

- Cache frequently accessed inventory
- Use Redis for distributed caching
- Implement cache invalidation on updates
- Consider read-through caching

### Concurrency

- Use database transactions for stock updates
- Implement optimistic locking for reservations
- Handle race conditions appropriately
- Use proper isolation levels

## Deployment Notes

### Production Configuration

1. **Database Connection Pool**: Configure HikariCP
2. **Connection Timeout**: Set appropriate timeout values
3. **RabbitMQ Configuration**: Use clustering for HA
4. **Monitoring**: Enable comprehensive metrics
5. **Alerting**: Set up low stock alerts

### Scaling

- Deploy multiple instances behind load balancer
- Use database read replicas
- Implement inventory sharding by product category
- Consider regional inventory distribution

## Troubleshooting

### Stock Inconsistencies

1. Verify database transaction isolation
2. Check for race conditions
3. Review reservation logic
4. Perform inventory audit

### Reservation Issues

1. Check reservation expiration logic
2. Verify RabbitMQ message delivery
3. Review confirmation/cancellation flow
4. Test with API client

### Low Stock Alerts Not Working

1. Verify RabbitMQ connection
2. Check notification service registration
3. Review event publishing
4. Test alert triggering manually

## Future Improvements

- [ ] Implement multi-warehouse support
- [ ] Add inventory forecasting
- [ ] Implement automatic restocking
- [ ] Add inventory audit trail
- [ ] Implement batch stock operations
- [ ] Add inventory reporting dashboard
- [ ] Implement demand-based allocation
- [ ] Add vendor management

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
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
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
