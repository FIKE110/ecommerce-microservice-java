# Product Service

Product catalog management microservice handling product CRUD operations, search, filtering, and inventory integration.

## Overview

The Product Service manages the complete product catalog including product details, categories, pricing, and search functionality. It provides RESTful APIs for product management and integrates with the Inventory Service for stock information.

## Features

- Product creation, update, and deletion
- Product retrieval by ID
- Paginated product listing with sorting
- Product search by name and category
- Price range filtering
- Integration with Inventory Service
- Product name and price retrieval
- Swagger/OpenAPI documentation

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Validation**: Jakarta Bean Validation
- **Security**: OAuth2 Resource Server
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **Tracing**: Zipkin + Brave
- **Documentation**: SpringDoc OpenAPI

## API Documentation

**Swagger UI**: http://localhost:8082/api/v1/product/docs
**OpenAPI JSON**: http://localhost:8082/api/v1/product/v3/api-docs

## Configuration

### Application Properties

```yaml
api-gateway-url: http://localhost:8081
spring:
  application:
    name: product-service
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

api:
  url: '/api/v1/product'

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

### Product Management Endpoints

#### Create Product
Create a new product (requires authentication).

```http
POST /api/v1/product
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Wireless Headphones",
  "description": "High-quality wireless headphones with noise cancellation",
  "category": "Electronics",
  "price": 149.99,
  "sku": "WH-001",
  "imageUrl": "https://example.com/images/headphones.jpg"
}
```

**Response**: `201 CREATED`

```json
{
  "code": "PRODUCT_CREATED",
  "data": {
    "message": "Product created successfully"
  }
}
```

#### Get Product by ID
Retrieve product details.

```http
GET /api/v1/product/{id}
```

**Response**: `200 OK`

```json
{
  "code": "PRODUCT_FETCHED",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Wireless Headphones",
    "description": "High-quality wireless headphones with noise cancellation",
    "category": "Electronics",
    "price": 149.99,
    "sku": "WH-001",
    "imageUrl": "https://example.com/images/headphones.jpg",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

#### Get Product Name
Retrieve only the product name.

```http
GET /api/v1/product/{id}/name
```

**Response**: `200 OK`

```json
{
  "name": "Wireless Headphones"
}
```

#### Get Product Price
Retrieve only the product price.

```http
GET /api/v1/product/{id}/price
```

**Response**: `200 OK`

```json
{
  "price": 149.99
}
```

#### List Products
Get paginated list of products with filtering and sorting.

```http
GET /api/v1/product?page=0&size=10&sort=createdAt&category=Electronics&minPrice=50&maxPrice=200&name=headphones
```

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort field (default: createdAt)
- `name` (optional): Filter by name (partial match)
- `category` (optional): Filter by category
- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price

**Response**: `200 OK`

```json
{
  "code": "PRODUCTS_FETCHED",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "Wireless Headphones",
        "description": "High-quality wireless headphones with noise cancellation",
        "category": "Electronics",
        "price": 149.99,
        "sku": "WH-001",
        "imageUrl": "https://example.com/images/headphones.jpg",
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

#### Update Product
Update an existing product (requires authentication).

```http
PUT /api/v1/product/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Premium Wireless Headphones",
  "description": "Updated description",
  "category": "Electronics",
  "price": 179.99,
  "sku": "WH-001",
  "imageUrl": "https://example.com/images/headphones-v2.jpg"
}
```

**Response**: `200 OK`

```json
{
  "code": "PRODUCT_UPDATED",
  "data": {
    "message": "Product is updated"
  }
}
```

#### Delete Product
Delete a product (requires authentication).

```http
DELETE /api/v1/product/{id}
Authorization: Bearer <access_token>
```

**Response**: `200 OK`

```json
{
  "code": "PRODUCT_DELETED",
  "data": {
    "message": "Product deleted"
  }
}
```

## Data Model

### Product Entity

```java
@Entity
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private String sku;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### ProductRequest DTO

```java
public record ProductRequest(
    String name,
    String description,
    String category,
    Double price,
    String sku,
    String imageUrl
) {}
```

### ProductResponseDto

```java
public record ProductResponseDto(
    UUID id,
    String name,
    String description,
    String category,
    Double price,
    String sku,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

## Search & Filtering

### Search by Name
Partial match search on product name:

```http
GET /api/v1/product?name=wireless
```

### Filter by Category
Exact match on category:

```http
GET /api/v1/product?category=Electronics
```

### Price Range
Filter products within price range:

```http
GET /api/v1/product?minPrice=50&maxPrice=200
```

### Combined Filters
All filters can be combined:

```http
GET /api/v1/product?name=wireless&category=Electronics&minPrice=100&maxPrice=200&page=0&size=20&sort=price
```

## Integration with Inventory Service

The Product Service publishes product events to RabbitMQ:

```java
@RabbitListener(queues = "${rabbitmq.queue.name}")
public void handleProductEvent(ProductEvent event) {
    // Handle product update events
}
```

### Event Types

- `PRODUCT_CREATED`: New product added
- `PRODUCT_UPDATED`: Product details updated
- `PRODUCT_DELETED`: Product removed

## Service Interactions

The Product Service communicates with:

- **Inventory Service**: Syncs product data for stock management
- **Order Service**: Provides product details for order creation
- **Cart Service**: Provides product details for cart display
- **Auth Service**: Validates JWT tokens for protected endpoints

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
| `PRODUCT_NOT_FOUND` | 404 | Product with specified ID does not exist |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Invalid or missing authentication |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `PRODUCT_ALREADY_EXISTS` | 409 | Product with same SKU already exists |

## Validation Rules

### Create/Update Product

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `name` | String | Yes | 1-255 characters |
| `description` | String | Yes | 1-5000 characters |
| `category` | String | Yes | 1-100 characters |
| `price` | Double | Yes | Must be positive |
| `sku` | String | Yes | 1-50 characters, unique |
| `imageUrl` | String | No | Valid URL |

## Running the Service

### Local Development

```bash
cd product-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd product-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t product-service:latest .

# Run container
docker run -p 8082:8082 product-service:latest
```

## Testing

### Run Tests

```bash
cd product-service
mvn test
```

### Test Coverage

- Unit tests for ProductService
- Integration tests for ProductController
- Search and filter tests
- Validation tests
- Integration with Inventory Service tests

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8082/actuator/health
- **Metrics**: http://localhost:8082/actuator/metrics
- **Info**: http://localhost:8082/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Performance Considerations

### Database Indexing

The following indexes are recommended:
- `idx_product_sku` (unique) - For SKU lookups
- `idx_product_category` - For category filtering
- `idx_product_name` - For name search
- `idx_product_price` - For price range queries

### Caching Strategy

Consider implementing caching for:
- Popular products
- Category listings
- Product search results

### Pagination

Always use pagination for listing products to avoid:
- Large result sets
- Memory issues
- Slow response times

## Deployment Notes

### Production Configuration

1. **Database Connection Pool**: Configure HikariCP settings
2. **Connection Timeout**: Set appropriate timeout values
3. **Query Optimization**: Enable query logging and optimization
4. **Index Strategy**: Create database indexes for performance
5. **Cache Configuration**: Set up Redis for product caching

### Scaling

- Deploy multiple instances behind load balancer
- Use read replicas for database
- Implement CDN for product images
- Consider Elasticsearch for advanced search

## Troubleshooting

### Slow Query Performance

1. Check database indexes
2. Review query execution plans
3. Optimize N+1 queries
4. Consider adding caching

### Product Not Found

1. Verify product ID format (UUID)
2. Check database for the product
3. Review logs for errors
4. Test with API client

### Validation Errors

1. Check request body format
2. Verify field constraints
3. Ensure required fields are present
4. Review validation error messages

## Future Improvements

- [ ] Implement advanced search with Elasticsearch
- [ ] Add product image upload functionality
- [ ] Implement product reviews and ratings
- [ ] Add product variants (size, color, etc.)
- [ ] Implement product recommendations
- [ ] Add bulk product operations
- [ ] Implement product versioning
- [ ] Add product export/import functionality

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
    <!-- ... -->
</dependencies>
```

---

For more information, see the [main project README](../README.md).
