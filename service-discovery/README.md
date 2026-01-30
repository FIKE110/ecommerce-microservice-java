# Service Discovery

Netflix Eureka server providing service registration and discovery for all microservices.

## Overview

The Service Discovery service (Eureka Server) acts as a central registry where all microservices register themselves. It enables dynamic service discovery, load balancing, and health monitoring of all services in the platform.

## Features

- Service registration and discovery
- Health monitoring of registered services
- Automatic service instance registration
- Instance metadata storage
- Service availability monitoring
- Dashboard for viewing registered services

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Discovery**: Netflix Eureka Server
- **Java Version**: 17

## Configuration

### Application Properties

```yaml
server:
  port: 8761

eureka:
  client:
    fetch-registry: false  # Eureka server doesn't register with itself
    register-with-eureka: false  # Eureka server doesn't register with itself

spring:
  application:
    name: service-discovery
  banner:
    location: banner.txt
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Eureka server port | `8761` |
| `EUREKA_CLIENT_FETCH_REGISTRY` | Fetch registry from other Eureka servers | `false` |
| `EUREKA_CLIENT_REGISTER_WITH_EUREKA` | Register with other Eureka servers | `false` |

## Eureka Dashboard

The Eureka server provides a web dashboard for monitoring registered services:

**URL**: http://localhost:8761

### Dashboard Features

- **List of registered services**: View all services and their instances
- **Instance details**: See instance metadata and health status
- **Service status**: Monitor availability and uptime
- **Service statistics**: View registration details

## Service Registration

### Client Configuration

All microservices configure Eureka client to register with the Eureka server:

```yaml
eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Instance Configuration

```yaml
spring:
  application:
    name: auth-service

eureka:
  instance:
    hostname: localhost
    lease-renewal-interval-in-seconds: 30  # Heartbeat interval
    lease-expiration-duration-in-seconds: 90  # Time before instance marked as unavailable
```

## Service Discovery Flow

### Registration Process

```
1. Service starts up
2. Service reads Eureka server URL from config
3. Service registers itself with Eureka
4. Eureka acknowledges registration
5. Service sends periodic heartbeats
```

### Discovery Process

```
1. Service A needs to call Service B
2. Service A queries Eureka for Service B instances
3. Eureka returns list of Service B instances
4. Service A selects an instance (load balancing)
5. Service A calls Service B directly
```

## Eureka Configuration Options

### Server Configuration

#### Self-Preservation Mode

Eureka can enter self-preservation mode if it loses contact with too many instances:

```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```

**Behavior**:
- Prevents Eureka from expiring instances due to network partitions
- Instances remain registered even if heartbeats are missed
- Ensures system continues operating during network issues

#### Eviction Interval

```yaml
eureka:
  server:
    eviction-interval-timer-in-ms: 60000  # 60 seconds
```

### Client Configuration

#### Heartbeat Configuration

```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30  # Send heartbeat every 30 seconds
    lease-expiration-duration-in-seconds: 90  # Instance expires after 90 seconds without heartbeat
```

#### Registry Fetch

```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 30  # Fetch registry every 30 seconds
```

## Service Metadata

### Instance Metadata

Services can provide metadata about themselves:

```yaml
eureka:
  instance:
    metadata-map:
      instanceId: ${spring.application.name}:${server.port}
      management.port: ${management.server.port}
      health.path: ${management.endpoints.web.base-path}/health
```

### Custom Metadata

```yaml
eureka:
  instance:
    metadata-map:
      version: "1.0.0"
      environment: ${spring.profiles.active}
      zone: "us-east-1"
```

## Monitoring

### Eureka Dashboard

Access the Eureka dashboard at: http://localhost:8761

The dashboard displays:
- **System Status**: Eureka server uptime, environment info
- **Instances Currently Registered**: Number of registered instances
- **Instances**: List of all services and their instances
- **Instance Details**: Hostname, status, metadata, last heartbeat

### Eureka REST API

#### Get All Applications

```bash
curl http://localhost:8761/eureka/apps
```

**Response**: XML listing all registered applications

#### Get Application by Name

```bash
curl http://localhost:8761/eureka/apps/AUTH-SERVICE
```

**Response**: XML listing all instances of auth-service

#### Get Application Instance

```bash
curl http://localhost:8761/eureka/apps/AUTH-SERVICE/hostname:port
```

**Response**: XML with instance details

#### Get Instance Status

```bash
curl http://localhost:8761/eureka/apps/AUTH-SERVICE/hostname:port/status
```

**Response**: Instance status (UP, DOWN, STARTING, OUT_OF_SERVICE)

### Actuator Endpoints

- **Health**: http://localhost:8761/actuator/health
- **Info**: http://localhost:8761/actuator/info

## Running the Service

### Local Development

```bash
cd service-discovery
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd service-discovery && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t service-discovery:latest .

# Run container
docker run -p 8761:8761 service-discovery:latest
```

## Testing

### Verify Registration

1. **Start Eureka Server**:
   ```bash
   cd service-discovery && mvn spring-boot:run
   ```

2. **Start Another Service** (e.g., auth-service):
   ```bash
   cd auth-service && mvn spring-boot:run
   ```

3. **Check Eureka Dashboard**:
   - Visit http://localhost:8761
   - Verify auth-service appears in the list

4. **Test REST API**:
   ```bash
   curl http://localhost:8761/eureka/apps
   ```

## Deployment Considerations

### High Availability

For production, deploy multiple Eureka servers in a cluster:

#### Eureka Server 1

```yaml
server:
  port: 8761

eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:8762/eureka
```

#### Eureka Server 2

```yaml
server:
  port: 8762

eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Client Configuration for HA

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka,http://eureka2:8762/eureka,http://eureka3:8763/eureka
```

### AWS Deployment

For AWS deployments, use DNS-based service discovery:

```yaml
eureka:
  instance:
    hostname: ${ec2.instance.public-hostname}
    prefer-ip-address: false
    non-secure-port-enabled: false
    secure-port-enabled: true
    secure-port: ${server.port}
  client:
    service-url:
      defaultZone: http://eureka-aws.yourdomain.com/eureka
```

## Troubleshooting

### Service Not Registering

1. **Check Eureka Server**: Verify it's running at http://localhost:8761
2. **Check Network**: Ensure service can reach Eureka server
3. **Check Configuration**: Verify `eureka.client.service-url.defaultZone`
4. **Check Logs**: Look for registration errors in service logs

### Instance Showing as DOWN

1. **Check Health Endpoint**: Verify service's `/actuator/health` endpoint
2. **Check Heartbeat**: Ensure service sends heartbeats (every 30 seconds)
3. **Check Network**: Verify connectivity between service and Eureka
4. **Check Logs**: Look for connection errors

### Eureka Server Not Starting

1. **Check Port**: Ensure port 8761 is not in use
2. **Check Logs**: Review Eureka server logs for errors
3. **Check Dependencies**: Verify Eureka server dependencies
4. **Check Java Version**: Ensure Java 17 is being used

### Instances Being Evicted

1. **Check Heartbeat Interval**: Verify `lease-renewal-interval-in-seconds`
2. **Check Network Latency**: High latency may cause missed heartbeats
3. **Check Self-Preservation**: Enable self-preservation for network issues
4. **Check Service Health**: Ensure services are healthy and responsive

## Best Practices

### 1. Service Naming

- Use lowercase, hyphenated names (e.g., `auth-service`, `product-service`)
- Keep names descriptive but concise
- Use consistent naming convention across services

### 2. Instance ID

- Use unique instance IDs for load balancing
- Include service name and random value or port
- Example: `${spring.application.name}:${spring.application.instance_id:${random.value}}}`

### 3. Health Checks

- Implement comprehensive health checks
- Include database, cache, and external service checks
- Ensure health checks return quickly (under 5 seconds)

### 4. Graceful Shutdown

- Implement graceful shutdown handlers
- De-register from Eureka before shutdown
- Allow in-flight requests to complete

### 5. Monitoring

- Monitor Eureka server health
- Track registration and deregistration events
- Set up alerts for service availability issues

## Future Improvements

- [ ] Implement Eureka clustering for high availability
- [ ] Add authentication to Eureka server
- [ ] Implement service versioning
- [ ] Add service dependency graph
- [ ] Implement service metrics collection
- [ ] Add Eureka backup and restore
- [ ] Implement service ranking/priority
- [ ] Add service deployment automation

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

## Additional Resources

- [Spring Cloud Netflix Eureka Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/spring-cloud-netflix.html)
- [Netflix Eureka Wiki](https://github.com/Netflix/eureka/wiki)

---

For more information, see: [main project README](../README.md).
