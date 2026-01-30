# Admin Server

Spring Boot Admin server providing centralized monitoring and management dashboard for all microservices.

## Overview

The Admin Server provides a web-based UI for monitoring and managing all Spring Boot applications in the platform. It displays health status, metrics, log levels, environment information, and allows runtime management of services.

## Features

- Real-time health monitoring of all services
- Metrics visualization (JVM, HTTP, DataSource, etc.)
- Log level management
- Environment configuration viewing
- Thread dump and heap dump
- Request tracing
- Application startup monitoring
- Service instance details

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Admin Server**: Spring Boot Admin 3.5.0
- **Service Discovery**: Netflix Eureka

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: admin-server

server:
  port: 9090

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Admin server port | `9090` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |

## Admin Dashboard

### Access Dashboard

**URL**: http://localhost:9090

The dashboard provides:

1. **Applications Overview**: List of all registered applications
2. **Application Details**: Health status, uptime, metrics
3. **Metrics**: JVM, HTTP requests, database connections
4. **Environment**: Configuration properties, system properties
5. **Logging**: View and change log levels
6. **Threads**: View active threads and their state
7. **Tracing**: View request traces (if Zipkin enabled)

### Dashboard Features

#### Applications List

- **Application Name**: Name of registered service
- **Instances**: Number of instances running
- **Status**: UP, DOWN, or UNKNOWN
- **Last Updated**: Last health check time
- **Version**: Application version (if configured)

#### Instance Details

- **Info**: Application information
- **Health**: Detailed health status
- **Metrics**: Various application metrics
- **Environment**: Configuration properties
- **Logging**: Log level management
- **Threads**: Thread dump
- **Heap Dump**: Memory snapshot
- **Mappings**: Request mappings
- **Scheduled Tasks**: Scheduled task information

## Service Registration

### Client Configuration

All microservices need to be configured to register with Admin Server:

#### Option 1: Auto-Discovery via Eureka (Recommended)

Admin Server automatically discovers services via Eureka:

```yaml
spring:
  boot:
    admin:
      client:
        instance:
          prefer-ip: true
```

#### Option 2: Direct Registration

Register services directly with Admin Server:

```yaml
spring:
  boot:
    admin:
      client:
        url: http://localhost:9090
        instance:
          prefer-ip: true
```

### Actuator Configuration

Services must expose actuator endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
```

## Monitoring Features

### Health Monitoring

Displays health status of each service:

- **UP**: Service is healthy
- **DOWN**: Service is unhealthy or unavailable
- **UNKNOWN**: Health status cannot be determined

Click on a service to see detailed health information:
- Database status
- Disk space
- External service connectivity
- Custom health indicators

### Metrics

The admin dashboard displays various metrics:

#### JVM Metrics
- Memory usage (heap, non-heap)
- Garbage collection
- Thread count
- Class loading
- CPU usage

#### Web Metrics
- HTTP requests
- Response times
- Error rates
- Active sessions

#### DataSource Metrics
- Active connections
- Idle connections
- Connection pool usage
- Query performance

#### System Metrics
- Uptime
- CPU usage
- System load
- File descriptors

### Log Management

View and change log levels dynamically:

- **View logs**: See recent log entries
- **Change log levels**: Set log levels for specific packages or classes
- **Clear logs**: Clear the log buffer
- **Download logs**: Download log files

Example: Change log level for a package:

```
com.fortune.auth.service: DEBUG
org.springframework.web: INFO
```

### Environment Configuration

View environment configuration:

- **System Properties**: JVM system properties
- **Environment Variables**: OS environment variables
- **Application Properties**: Configured application properties
- **Configuration Properties**: Spring configuration properties

### Thread Dump

Generate and view thread dumps:

- **Active Threads**: Currently running threads
- **Thread State**: Thread states (RUNNABLE, WAITING, BLOCKED, etc.)
- **Stack Traces**: Thread stack traces
- **Deadlock Detection**: Detect potential deadlocks

### Heap Dump

Generate heap dump for memory analysis:

- **Memory Snapshot**: Complete memory dump
- **Object Analysis**: Analyze object counts and sizes
- **Memory Leaks**: Detect potential memory leaks
- **GC Analysis**: Garbage collection statistics

## Security Configuration

### Basic Authentication

Enable basic authentication for Admin Server:

```yaml
spring:
  security:
    user:
      name: admin
      password: admin123
```

### Client Authentication

Configure services to authenticate with Admin Server:

```yaml
spring:
  boot:
    admin:
      client:
        username: admin
        password: admin123
```

### Custom Authentication

Implement custom authentication using Spring Security:

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll())
            .build();
    }
}
```

## Running the Service

### Local Development

```bash
cd admin-server
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd admin-server && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t admin-server:latest .

# Run container
docker run -p 9090:9090 admin-server:latest
```

## Testing

### Verify Registration

1. **Start Admin Server**:
   ```bash
   cd admin-server && mvn spring-boot:run
   ```

2. **Start Other Services**:
   ```bash
   cd auth-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   ```

3. **Access Dashboard**:
   - Visit http://localhost:9090
   - Verify services appear in the list

### Test Actuator Endpoints

```bash
# Check health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/metrics

# Check environment
curl http://localhost:8080/actuator/env
```

## Monitoring and Alerts

### Custom Metrics

Add custom metrics to your services:

```java
@Component
public class CustomMetrics {
    private final MeterRegistry registry;
    private final Counter orderCounter;

    public CustomMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.orderCounter = Counter.builder("orders.created")
            .description("Number of orders created")
            .register(registry);
    }

    public void recordOrder() {
        orderCounter.increment();
    }
}
```

### Health Indicators

Add custom health indicators:

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check database connectivity
        boolean isHealthy = checkDatabase();

        if (isHealthy) {
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .build();
        } else {
            return Health.down()
                .withDetail("error", "Cannot connect to database")
                .build();
        }
    }
}
```

## Deployment Considerations

### High Availability

Deploy multiple Admin Server instances behind a load balancer:

```yaml
eureka:
  instance:
    metadata-map:
      user.name: ${SECURITY_USER_NAME:admin}
      user.password: ${SECURITY_USER_PASSWORD:admin}
```

### Security

- Enable HTTPS/TLS for production
- Use strong authentication mechanism
- Implement role-based access control
- Restrict access to admin endpoints

### Performance

- Configure appropriate refresh intervals
- Limit the number of concurrent connections
- Use caching for frequently accessed data
- Enable compression for large responses

## Troubleshooting

### Services Not Appearing

1. **Check Eureka**: Verify services are registered in Eureka
2. **Check Actuator**: Verify actuator endpoints are exposed
3. **Check Network**: Ensure Admin Server can reach services
4. **Check Logs**: Review Admin Server logs for errors

### Health Status Showing DOWN

1. **Check Service Health**: Access `/actuator/health` directly
2. **Check Dependencies**: Verify database, Redis, and other dependencies
3. **Check Logs**: Review service logs for health check failures
4. **Check Configuration**: Verify health check configuration

### Metrics Not Displaying

1. **Check Metrics Endpoint**: Access `/actuator/metrics` directly
2. **Check Metrics Configuration**: Verify metrics are enabled
3. **Check Permissions**: Ensure user has access to metrics
4. **Check Logs**: Review Admin Server logs for metric errors

### Log Level Changes Not Persisting

1. **Check Configuration**: Verify log level configuration is correct
2. **Check Framework**: Some frameworks override log levels
3. **Check Logging System**: Verify logging system supports runtime changes
4. **Restart Service**: Restart the service to apply changes

## Best Practices

### 1. Actuator Security

- Disable sensitive endpoints in production
- Use authentication and authorization
- Monitor actuator access logs
- Restrict access to admin endpoints

### 2. Health Checks

- Implement comprehensive health checks
- Include database, cache, and external service checks
- Ensure health checks return quickly
- Use liveness and readiness probes (for Kubernetes)

### 3. Metrics

- Monitor key business metrics
- Track performance metrics
- Set up alerts for critical metrics
- Export metrics to external systems (Prometheus, Grafana)

### 4. Logging

- Use structured logging (JSON format)
- Set appropriate log levels for production
- Implement log rotation
- Centralize logs for analysis

## Future Improvements

- [ ] Implement role-based access control
- [ ] Add custom dashboards
- [ ] Implement alerting and notifications
- [ ] Add support for custom plugins
- [ ] Implement log aggregation
- [ ] Add performance profiling
- [ ] Implement incident management
- [ ] Add support for custom themes

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-server</artifactId>
    </dependency>
</dependencies>
```

## Additional Resources

- [Spring Boot Admin Documentation](https://docs.spring-boot-admin.com/)
- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

For more information, see: [main project README](../README.md).
