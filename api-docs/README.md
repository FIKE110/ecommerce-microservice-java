# API Docs

Aggregated API documentation portal providing unified documentation for all microservices.

## Overview

The API Docs service provides a centralized documentation hub for all microservices in the e-commerce platform. It aggregates Swagger/OpenAPI documentation from all services and presents them in a unified interface.

## Features

- Aggregated API documentation
- Service-specific documentation views
- Interactive API testing (Try It Out)
- OpenAPI 3.0 specification
- Swagger UI integration
- Auto-discovery of service documentation
- Documentation versioning

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Documentation**: SpringDoc OpenAPI
- **Service Discovery**: Netflix Eureka
- **UI Library**: Swagger UI
- **Template Engine**: Thymeleaf

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: api-docs
  cloud:
    discovery:
      enabled: true

server:
  port: 8080

swagger:
  enabled: true
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | API Docs server port | `8080` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `SWAGGER_ENABLED` | Enable Swagger UI | `true` |

## Documentation Structure

### Service Documentation URLs

Each service provides its own OpenAPI documentation at:

| Service | Documentation URL | API Docs Path |
|---------|------------------|----------------|
| **Auth Service** | http://localhost:8080/api/v1/auth/docs | `/api/v1/auth/v3/api-docs` |
| **Product Service** | http://localhost:8082/api/v1/product/docs | `/api/v1/product/v3/api-docs` |
| **Order Service** | http://localhost:8084/api/v1/order/docs | `/api/v1/order/v3/api-docs` |
| **Cart Service** | http://localhost:8087/api/v1/cart/docs | `/api/v1/cart/v3/api-docs` |
| **Customer Service** | http://localhost:8090/api/v1/customer/docs | `/api/v1/customer/v3/api-docs` |
| **Inventory Service** | http://localhost:8083/api/v1/inventory/docs | `/api/v1/inventory/v3/api-docs` |
| **Payment Service** | http://localhost:8085/api/v1/payment/docs | `/api/v1/payment/v3/api-docs` |

## API Documentation Access

### Main Documentation Hub

**URL**: http://localhost:8080/swagger-ui.html

This provides an overview of all available services and their APIs.

### Individual Service Documentation

Access each service's documentation:

**Auth Service**: http://localhost:8080/api/v1/auth/docs
**Product Service**: http://localhost:8082/api/v1/product/docs
**Order Service**: http://localhost:8084/api/v1/order/docs
**Cart Service**: http://localhost:8087/api/v1/cart/docs

### OpenAPI JSON Specs

Access raw OpenAPI JSON specifications:

```bash
# Auth Service
curl http://localhost:8080/api/v1/auth/v3/api-docs

# Product Service
curl http://localhost:8082/api/v1/product/v3/api-docs

# Order Service
curl http://localhost:8084/api/v1/order/v3/api-docs
```

## Using the Documentation

### Interactive API Testing

1. **Select an endpoint** from the list
2. **Click "Try it out"**
3. **Fill in parameters** (path, query, body)
4. **Add authentication** (if required):
   ```http
   Authorization: Bearer <your-jwt-token>
   ```
5. **Click "Execute"** to send request
6. **View response** in the Response section

### Authentication

For protected endpoints, you need to include a JWT token:

1. First, authenticate via Auth Service:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/sign-in \
     -H "Content-Type: application/json" \
     -d '{"username": "johndoe", "password": "password"}'
   ```

2. Copy the `access_token` from the response

3. Add token to Swagger UI:
   - Click "Authorize" button
   - Enter: `Bearer <your-token>`
   - Click "Authorize"
   - Close the dialog

### Request/Response Examples

Each endpoint includes:
- **Description**: What the endpoint does
- **Parameters**: Required and optional parameters
- **Request Body**: Example request payload
- **Response**: Example response format
- **Response Codes**: HTTP status codes and meanings

## Documentation Configuration

### OpenAPI Configuration (Service Level)

Each service configures its OpenAPI documentation:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Service API")
                .version("1.0.0")
                .description("Product catalog and management APIs")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081")
                    .description("API Gateway")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
```

### API Documentation Annotations

Use SpringDoc annotations to enhance documentation:

```java
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    @Operation(
        summary = "Create a new product",
        description = "Create a new product in the catalog",
        tags = { "Product Management" }
    )
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping
    public ResponseEntity<?> createProduct(
        @Parameter(
            description = "Product details",
            required = true,
            schema = @Schema(implementation = ProductRequest.class)
        )
        @RequestBody @Valid ProductRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        // Implementation
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a product by its UUID"
    )
    @Parameter(
        name = "id",
        description = "Product ID",
        required = true
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(
        @PathVariable UUID id
    ) {
        // Implementation
    }
}
```

## Service Discovery Integration

The API Docs service uses Eureka to discover services:

```java
@Service
public class ServiceDiscoveryService {

    @Autowired
    private DiscoveryClient discoveryClient;

    public List<ServiceInstance> getServices() {
        return discoveryClient.getInstances("auth-service");
    }

    public String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new ServiceNotFoundException(serviceName);
        }
        return instances.get(0).getUri().toString();
    }
}
```

## Running the Service

### Local Development

```bash
cd api-docs
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd api-docs && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t api-docs:latest .

# Run container
docker run -p 8080:8080 api-docs:latest
```

## Accessing Documentation

Once the service is running:

1. **Main Swagger UI**: http://localhost:8080/swagger-ui.html
2. **API JSON**: http://localhost:8080/v3/api-docs
3. **Actuator Health**: http://localhost:8080/actuator/health

## Testing Documentation

### Verify Documentation is Working

```bash
# Test Auth Service docs
curl http://localhost:8080/api/v1/auth/v3/api-docs | jq '.info.title'

# Test Product Service docs
curl http://localhost:8082/api/v1/product/v3/api-docs | jq '.info.title'

# Test individual endpoint
curl http://localhost:8082/api/v1/product | jq
```

### Testing API from Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. Expand an endpoint (e.g., POST /api/v1/auth/sign-in)
3. Click "Try it out"
4. Enter request body:
   ```json
   {
     "username": "johndoe",
     "password": "password"
   }
   ```
5. Click "Execute"
6. View the response

## Documentation Best Practices

### 1. Clear Descriptions

- Write clear, concise descriptions
- Include what the endpoint does
- Explain parameters clearly
- Document response codes

### 2. Examples

- Provide example requests
- Include example responses
- Show common scenarios
- Document error responses

### 3. Consistent Naming

- Use consistent naming conventions
- Group related endpoints
- Use clear parameter names
- Follow REST principles

### 4. Security Documentation

- Clearly mark secured endpoints
- Document required scopes/roles
- Show authentication examples
- Explain token format

### 5. Versioning

- Document API version changes
- Maintain backward compatibility notes
- Document deprecation timelines
- Provide migration guides

## Troubleshooting

### Documentation Not Loading

1. Verify service is running: `curl http://localhost:8080/actuator/health`
2. Check Eureka registration: Visit http://localhost:8761
3. Review service logs for errors
4. Clear browser cache

### Services Not Appearing in Docs

1. Verify services are running
2. Check services are registered in Eureka
3. Ensure services expose OpenAPI endpoints
4. Review service configuration

### Swagger UI Not Loading

1. Check browser console for JavaScript errors
2. Verify SpringDoc dependency is correct
3. Check service port configuration
4. Test with different browser

## Future Improvements

- [ ] Implement API versioning (v1, v2, etc.)
- [ ] Add API analytics (usage tracking)
- [ ] Implement documentation search
- [ ] Add code examples in multiple languages
- [ ] Implement documentation authentication
- [ ] Add API deprecation notices
- [ ] Implement documentation feedback system
- [ ] Add documentation change history

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.12</version>
    </dependency>
</dependencies>
```

## Additional Resources

- [OpenAPI Specification](https://swagger.io/specification/)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

---

For more information, see: [main project README](../README.md).
