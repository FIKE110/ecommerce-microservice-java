# Cart Service

Shopping cart management microservice providing real-time cart operations using Redis for persistence.

## Overview

The Cart Service manages customer shopping carts with Redis for high-performance, real-time storage. It handles cart operations like adding items, updating quantities, removing items, and checkout integration with the Order Service.

## Features

- Add products to cart
- Update product quantities
- Remove individual items
- Clear entire cart
- Cart checkout integration
- Redis-based storage for performance
- Real-time cart updates
- Automatic cart expiration
- JWT-based authentication

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Cache**: Redis
- **Database**: PostgreSQL (for backup/persistence)
- **Security**: OAuth2 Resource Server
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **HTTP Client**: OpenFeign
- **Tracing**: Zipkin + Brave

## API Documentation

**Swagger UI**: http://localhost:8087/api/v1/cart/docs
**OpenAPI JSON**: http://localhost:8087/api/v1/cart/v3/api-docs

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: cart-service
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

api:
  url: '/api/v1/cart'

rsa:
  public-key: classpath:certs/public.pem

server:
  port: 0  # Random port assigned by Eureka
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/ecommercems` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` |
| `RSA_PUBLIC_KEY` | Path to RSA public key | `classpath:certs/public.pem` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `CART_EXPIRY_MINUTES` | Cart expiration time | `1440` (24 hours) |

## API Endpoints

### Cart Management Endpoints

#### Get Cart
Retrieve current user's cart contents.

```http
GET /api/v1/cart
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "550e8400-e29b-41d4-a716-446655440000": 2,
  "660e9500-e39b-51d4-a816-556655550000": 1
}
```

The response is a map where:
- **Key**: Product ID (UUID)
- **Value**: Quantity (Long)

#### Add Product to Cart
Add a product to cart with default quantity of 1.

```http
POST /api/v1/cart/{productId}/add
Authorization: Bearer <access_token>
```

**Response**: `204 NO CONTENT`

#### Add Product with Quantity
Add a product to cart with specified quantity.

```http
POST /api/v1/cart/{productId}/add/{quantity}
Authorization: Bearer <access_token>
```

**Parameters**:
- `productId` (path): Product UUID
- `quantity` (path): Quantity to add

**Response**: `204 NO CONTENT`

**Behavior**:
- If product exists in cart, adds to existing quantity
- If product doesn't exist, creates new entry
- Updates cart expiration time

#### Decrease Product Quantity
Decrease quantity of a product in cart by 1.

```http
POST /api/v1/cart/{productId}/remove
Authorization: Bearer <access_token>
```

**Response**: `204 NO CONTENT`

**Behavior**:
- Reduces product quantity by 1
- If quantity reaches 0, removes product from cart
- Updates cart expiration time

#### Remove Product from Cart
Remove a product entirely from cart.

```http
POST /api/v1/cart/{productId}/delete
Authorization: Bearer <access_token>
```

**Response**: `204 NO CONTENT`

**Behavior**:
- Removes product from cart regardless of quantity
- Updates cart expiration time

#### Checkout
Process cart checkout and create order.

```http
POST /api/v1/cart/checkout
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "checkout_url": "https://checkout.payment-provider.com/pay/abc123"
}
```

**Checkout Flow**:
1. Retrieves current cart contents
2. Validates all products exist and are available
3. Creates order in Order Service
4. Initializes payment via Payment Service
5. Returns checkout URL
6. Clears cart on successful payment

## Data Model

### Cart Entity (Redis)

```
Key: cart:{userId}
Type: Hash
Fields:
  - {productId}: quantity
TTL: 24 hours (configurable)
```

### Example Redis Storage

```
HGETALL cart:johndoe
550e8400-e29b-41d4-a716-446655440000 => "2"
660e9500-e39b-51d4-a816-556655550000 => "1"
```

## Redis Configuration

### Connection Pooling

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }
}
```

### Cart Expiration

```java
public void addToCart(Jwt jwt, String productId) {
    String userId = jwt.getSubject();
    redisTemplate.opsForHash().increment(getCartKey(userId), productId, 1);
    redisTemplate.expire(getCartKey(userId), Duration.ofHours(24));
}
```

## Service Interactions

The Cart Service communicates with:

- **Auth Service**: Validates JWT tokens
- **Product Service**: Validates product existence and retrieves details
- **Order Service**: Creates order during checkout
- **Payment Service**: Processes payment during checkout

## Cart Operations

### Add to Cart Logic

```java
public void addToCart(Jwt jwt, String productId, Long quantity) {
    String userId = jwt.getSubject();
    String cartKey = getCartKey(userId);

    // Check if product exists
    validateProduct(productId);

    // Add or update quantity
    redisTemplate.opsForHash().putIfAbsent(cartKey, productId, 0L);
    redisTemplate.opsForHash().increment(cartKey, productId, quantity);

    // Update expiration
    redisTemplate.expire(cartKey, Duration.ofHours(24));
}
```

### Checkout Logic

```java
public String checkout(Jwt jwt) {
    String userId = jwt.getSubject();
    String cartKey = getCartKey(userId);

    // Get cart items
    Map<Object, Object> items = redisTemplate.opsForHash().entries(cartKey);

    // Validate items
    validateCartItems(items);

    // Create order
    String checkoutUrl = orderClient.createOrder(userId, items);

    // Clear cart
    redisTemplate.delete(cartKey);

    return checkoutUrl;
}
```

## Error Handling

### Error Response Format

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "User-friendly error message"
  }
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `CART_NOT_FOUND` | 404 | User's cart does not exist |
| `PRODUCT_NOT_FOUND` | 404 | Product does not exist |
| `PRODUCT_OUT_OF_STOCK` | 400 | Product is out of stock |
| `INVALID_QUANTITY` | 400 | Invalid quantity specified |
| `CART_EMPTY` | 400 | Cannot checkout with empty cart |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |

## Validation Rules

### Add Product

| Field | Type | Constraints |
|-------|------|-------------|
| `productId` | UUID | Must exist in Product Service |
| `quantity` | Long | Must be positive (optional, default: 1) |

### Checkout

- Cart must not be empty
- All products must exist and be available
- Total quantity must be valid

## Running the Service

### Local Development

```bash
cd cart-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd cart-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t cart-service:latest .

# Run container
docker run -p 8087:8087 cart-service:latest
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
cd cart-service
mvn test
```

### Test Coverage

- Unit tests for CartService
- Integration tests for CartController
- Redis operations tests
- Checkout integration tests
- Error handling tests

### Manual Testing with Redis CLI

```bash
# Connect to Redis
redis-cli

# View all carts
KEYS cart:*

# View specific cart
HGETALL cart:johndoe

# Delete cart
DEL cart:johndoe
```

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8087/actuator/health
- **Metrics**: http://localhost:8087/actuator/metrics
- **Info**: http://localhost:8087/actuator/info

### Redis Monitoring

```bash
# Monitor Redis operations
redis-cli monitor

# Check Redis info
redis-cli info stats
redis-cli info memory
```

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Performance Considerations

### Redis Optimization

- Use connection pooling (Lettuce)
- Enable pipelining for bulk operations
- Use Redis Cluster for horizontal scaling
- Monitor Redis memory usage
- Set appropriate TTL values

### Cache Strategy

- Store only product IDs and quantities in Redis
- Retrieve product details from Product Service
- Implement local caching for frequent queries
- Consider Redis eviction policies

### Scalability

- Redis can handle millions of operations per second
- Use Redis Sentinel for high availability
- Use Redis Cluster for horizontal scaling
- Implement read replicas for load balancing

## Deployment Notes

### Production Configuration

1. **Redis Configuration**:
   - Use Redis Sentinel or Cluster for HA
   - Enable Redis authentication
   - Use TLS for secure connections
   - Configure persistence (RDB/AOF)

2. **Connection Pool**:
   - Increase max-active connections
   - Configure connection timeout
   - Enable connection reuse

3. **Monitoring**:
   - Monitor Redis memory usage
   - Track cart operations metrics
   - Set up alerts for Redis failures

### Scaling

- Redis Cluster for high availability
- Load balance multiple service instances
- Use read replicas for read-heavy workloads
- Implement cart sharding by user ID

## Troubleshooting

### Redis Connection Issues

1. Verify Redis is running: `redis-cli ping`
2. Check connection string in application.yml
3. Verify firewall settings
4. Review connection pool configuration

### Cart Not Found

1. Check if cart key exists: `HGETALL cart:{userId}`
2. Verify cart hasn't expired (TTL)
3. Check Redis memory: `redis-cli info memory`
4. Review logs for errors

### Checkout Failures

1. Verify cart is not empty
2. Check product availability
3. Review payment integration logs
4. Test with API client

### Performance Issues

1. Monitor Redis slow log: `slowlog get`
2. Check connection pool stats
3. Review Redis memory usage
4. Optimize frequent operations

## Future Improvements

- [ ] Implement cart persistence to database
- [ ] Add cart sharing functionality
- [ ] Implement saved carts/wishlists
- [ ] Add cart analytics
- [ ] Implement cart recommendations
- [ ] Add cart versioning for optimistic locking
- [ ] Implement cart merge for guest users
- [ ] Add cart backup and restore

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
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
    <!-- ... -->
</dependencies>
```

## Redis Commands Reference

### Common Commands

```bash
# Connect to Redis
redis-cli

# Get all cart keys
KEYS cart:*

# Get cart contents
HGETALL cart:{userId}

# Get specific product quantity
HGET cart:{userId} {productId}

# Delete cart
DEL cart:{userId}

# Set cart TTL
EXPIRE cart:{userId} 86400

# Get cart TTL
TTL cart:{userId}
```

---

For more information, see: [main project README](../README.md).
