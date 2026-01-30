# API Gateway

API Gateway providing unified entry point for all microservices with routing, load balancing, security, and cross-cutting concerns.

## Overview

The API Gateway acts as the single entry point for all client requests. It routes requests to appropriate backend services, handles cross-cutting concerns like security, logging, rate limiting, and provides a unified API interface to clients.

## Features

- Dynamic routing based on service discovery
- Load balancing across service instances
- JWT authentication and authorization
- Request/response logging and tracing
- Circuit breaking and retry mechanisms
- Rate limiting
- CORS handling
- Service-to-service communication

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Gateway**: Spring Cloud Gateway (WebFlux)
- **Service Discovery**: Netflix Eureka
- **Load Balancer**: Spring Cloud LoadBalancer
- **Tracing**: Zipkin + Brave
- **Monitoring**: Spring Boot Actuator

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      loadbalancer:
        ribbon:
          enabled: false

server:
  port: 8081
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `SERVER_PORT` | Gateway port | `8081` |
| `ZIPKIN_TRACING_ENDPOINT` | Zipkin tracing URL | `http://localhost:9411/api/v2/spans` |

## Route Configuration

Routes can be configured via:

### 1. Code-based Configuration

```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r
                .path("/api/v1/auth/**")
                .filters(f -> f.stripPrefix(0))
                .uri("lb://auth-service"))
            .route("product-service", r -> r
                .path("/api/v1/product/**")
                .filters(f -> f.stripPrefix(0))
                .uri("lb://product-service"))
            .build();
    }
}
```

### 2. Properties-based Configuration

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/v1/auth/**
          filters:
            - StripPrefix=0

        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/v1/product/**
          filters:
            - StripPrefix=0
```

## Route Definitions

### Auth Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/auth/**` | auth-service | Authentication endpoints |

### Product Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/product/**` | product-service | Product catalog endpoints |

### Order Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/order/**` | order-service | Order management endpoints |

### Cart Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/cart/**` | cart-service | Shopping cart endpoints |

### Customer Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/customer/**` | customer-service | Customer profile endpoints |

### Payment Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/payment/**` | payment-service | Payment processing endpoints |

### Inventory Service Routes

| Path Pattern | Target Service | Description |
|--------------|---------------|-------------|
| `/api/v1/inventory/**` | inventory-service | Inventory management endpoints |

## Gateway Filters

### Built-in Filters

#### StripPrefix Filter

Removes specified number of path segments before routing:

```yaml
filters:
  - StripPrefix=0
```

#### AddRequestHeader Filter

Adds a header to the request:

```yaml
filters:
  - AddRequestHeader=X-Request-Id, ${random.uuid}
```

#### Retry Filter

Retries failed requests:

```yaml
filters:
  - Retry=3,HTTP_5XX
```

#### RateLimiter Filter

Limits request rate:

```yaml
filters:
  - name: RateLimiter
    args:
      redis-rate-limiter.replenishRate: 10
      redis-rate-limiter.burstCapacity: 20
```

### Custom Filters

#### Logging Filter

```java
@Component
public class LoggingFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.info("Incoming request: {}", path);
        return chain.filter(exchange);
    }
}
```

#### Authentication Filter

```java
@Component
public class AuthenticationFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
```

## Security

### JWT Authentication

The gateway validates JWT tokens before routing requests:

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/auth/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}
```

### Public Endpoints

The following endpoints don't require authentication:
- `/api/v1/auth/sign-up`
- `/api/v1/auth/sign-in`
- `/api/v1/auth/send-otp`
- `/actuator/**`

## Service Discovery Integration

The gateway uses Eureka for service discovery:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

### Load Balancing

Requests are load balanced across multiple service instances using Spring Cloud LoadBalancer:

```java
@Bean
public ReactiveLoadBalancer<ServiceInstance> loadBalancer(
    LoadBalancerClientFactory factory
) {
    return factory.getInstance("service-name");
}
```

## Circuit Breaking

### Resilience4j Circuit Breaker

```java
@Configuration
public class CircuitBreakerConfig {
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory
            .configureDefault(id -> Resilience4JConfigBuilder.of(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.ofDefaults())
                .build());
    }
}
```

### Circuit Breaker Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      backendA:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
```

## Tracing

### Distributed Tracing with Zipkin

All requests are traced and sent to Zipkin:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### Custom Headers

The gateway adds tracing headers:

```yaml
filters:
  - AddRequestHeader=X-Trace-Id, ${traceId}
  - AddRequestHeader=X-Span-Id, ${spanId}
```

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics
- **Gateway Routes**: http://localhost:8081/actuator/gateway/routes
- **Gateway Global Filters**: http://localhost:8081/actuator/gateway/globalfilters
- **Gateway Predicate Handlers**: http://localhost:8081/actuator/gateway/predicates

### Gateway Metrics

```bash
# View all gateway metrics
curl http://localhost:8081/actuator/metrics/spring.cloud.gateway.requests

# View route-specific metrics
curl http://localhost:8081/actuator/metrics/spring.cloud.gateway.requests?tag=routeId:auth-service
```

## Error Handling

### Global Error Handler

```java
@Component
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        HttpStatus status = determineStatus(error);

        return ServerResponse.status(status)
            .bodyValue(Map.of(
                "error", error.getMessage(),
                "status", status.value()
            ));
    }
}
```

## CORS Configuration

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
```

## Running the Service

### Local Development

```bash
cd api-gateway
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd api-gateway && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t api-gateway:latest .

# Run container
docker run -p 8081:8081 api-gateway:latest
```

## Testing

### Run Tests

```bash
cd api-gateway
mvn test
```

### Manual Testing

```bash
# Test routing to auth service
curl http://localhost:8081/api/v1/auth/profile?username=johndoe

# Test routing to product service
curl http://localhost:8081/api/v1/product

# Test with JWT token
curl -H "Authorization: Bearer <token>" http://localhost:8081/api/v1/cart
```

## Deployment Notes

### Production Configuration

1. **Service Discovery**:
   - Configure multiple Eureka servers
   - Enable service discovery caching
   - Set appropriate timeouts

2. **Security**:
   - Enable HTTPS/TLS
   - Use secure JWT keys from vault
   - Configure CORS for production domains

3. **Performance**:
   - Increase connection pool size
   - Enable response compression
   - Configure timeouts appropriately

4. **Monitoring**:
   - Enable comprehensive metrics
   - Set up alerts for circuit breakers
   - Monitor gateway health

### Scaling

- Deploy multiple instances behind load balancer
- Use sticky sessions if needed (rare with stateless JWT)
- Configure health checks for load balancer
- Implement blue-green deployment

## Troubleshooting

### Route Not Found

1. Check Eureka for service registration
2. Verify route configuration
3. Review logs for routing errors
4. Test service availability directly

### Authentication Failures

1. Verify JWT token format
2. Check public key configuration
3. Review Auth Service availability
4. Test with valid token

### Circuit Breaker Issues

1. Monitor circuit breaker state
2. Check backend service health
3. Review circuit breaker configuration
4. Test backend service directly

## Future Improvements

- [ ] Implement API versioning
- [ ] Add GraphQL gateway
- [ ] Implement request/response transformation
- [ ] Add API key authentication
- [ ] Implement websocket support
- [ ] Add API analytics dashboard
- [ ] Implement rate limiting per user
- [ ] Add request/response caching

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-gateway-server-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>io.zipkin.reporter2</groupId>
        <artifactId>zipkin-reporter-brave</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-brave</artifactId>
    </dependency>
</dependencies>
```

---

For more information, see: [main project README](../README.md).
