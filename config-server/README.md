# Config Server

Centralized configuration management server using Spring Cloud Config for all microservices.

## Overview

The Config Server provides externalized configuration management for all microservices in the platform. It allows configuration to be stored and versioned centrally, enabling dynamic configuration updates without restarting services.

## Features

- Centralized configuration management
- Version control integration (Git, SVN, etc.)
- Environment-specific configuration
- Dynamic configuration updates
- Configuration encryption/decryption
- Multiple configuration sources
- Configuration server clustering
- Health and status monitoring

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Config Server**: Spring Cloud Config
- **Java Version**: 17

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: file://${user.home}/config-repo
          search-paths: configs
          default-label: main
          clone-on-start: true

server:
  port: 8888
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Config server port | `8888` |
| `SPRING_CLOUD_CONFIG_SERVER_GIT_URI` | Git repository URI | `file://${user.home}/config-repo` |
| `SPRING_CLOUD_CONFIG_SERVER_GIT_SEARCH_PATHS` | Search paths | `configs` |
| `SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULT_LABEL` | Default branch | `main` |

## Configuration Repository Structure

### Directory Structure

```
config-repo/
├── configs/
│   ├── auth-service.yml
│   ├── auth-service-dev.yml
│   ├── auth-service-prod.yml
│   ├── product-service.yml
│   ├── product-service-dev.yml
│   ├── product-service-prod.yml
│   └── application.yml
└── README.md
```

### Configuration File Naming Convention

- `{application}.yml`: Default configuration for all environments
- `{application}-{profile}.yml`: Environment-specific configuration
- `application.yml`: Default configuration shared by all applications

Example:
```
auth-service.yml          # Default auth-service config
auth-service-dev.yml     # Development environment config
auth-service-prod.yml    # Production environment config
```

## Service Configuration

### Example: auth-service.yml

```yaml
app:
  title: "AUTH-SERVICE"
  version: "1.0.0"
  description: "API documentation for My Auth Service Microservice"
  url: http://localhost:8081

api:
  url: '/api/v1/auth'

rsa:
  private-key: classpath:certs/private.pem
  public-key: classpath:certs/public.pem

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommercems
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: '*'
```

### Example: auth-service-prod.yml

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false

logging:
  level:
    root: INFO
    com.fortune: WARN
```

## Client Configuration

### Bootstrap Configuration

Services use `bootstrap.yml` (or `bootstrap.properties`) to connect to Config Server:

```yaml
spring:
  application:
    name: auth-service
  profiles:
    active: dev
  config:
    import: configserver:http://localhost:8888
```

### Alternative: application.yml

```yaml
spring:
  application:
    name: auth-service
  profiles:
    active: dev
  config:
    import: optional:configserver:http://localhost:8888
```

### Environment Variables Override

```yaml
spring:
  config:
    import: configserver:${CONFIG_SERVER_URL:http://localhost:8888}
```

## Configuration Access

### REST API

#### Get Application Configuration

```bash
curl http://localhost:8888/auth-service/default
```

**Response**: JSON with configuration properties

#### Get Environment-Specific Configuration

```bash
curl http://localhost:8888/auth-service/prod
```

**Response**: JSON with production configuration

#### Get Specific Property

```bash
curl http://localhost:8888/auth-service/default/app.title
```

**Response**: `"AUTH-SERVICE"`

### Response Format

```json
{
  "name": "auth-service",
  "profiles": ["default"],
  "label": null,
  "version": "abc123",
  "state": null,
  "propertySources": [
    {
      "name": "config-repo/configs/auth-service.yml",
      "source": {
        "app.title": "AUTH-SERVICE",
        "api.url": "/api/v1/auth"
      }
    }
  ]
}
```

## Configuration Encryption

### Encryption Keys

Generate encryption keys:

```bash
# Generate key
keytool -genseckey \
  -alias mykey \
  -keyalg AES \
  -keysize 128 \
  -storetype JCEKS \
  -keystore config-server.jks

# Add to application.yml
encrypt:
  keyStore:
    location: classpath:config-server.jks
    password: mypassword
    alias: mykey
```

### Encrypt Properties

Encrypt sensitive values:

```bash
curl http://localhost:8888/encrypt -d "my-password"
```

**Response**: Encrypted value (e.g., `AQA1...`)

### Use in Configuration

Use encrypted values in configuration files:

```yaml
spring:
  datasource:
    password: '{cipher}AQA1...'
```

The Config Server will automatically decrypt the value.

## Configuration Refresh

### Manual Refresh

Enable `/refresh` endpoint:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh,health,info,beans
```

Refresh configuration:

```bash
curl -X POST http://localhost:8080/actuator/refresh
```

**Response**: List of refreshed properties

```json
[
  "spring.datasource.password",
  "app.title"
]
```

### Auto Refresh with Spring Cloud Bus

Add Spring Cloud Bus dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

Publish refresh event:

```bash
curl -X POST http://localhost:8888/actuator/busrefresh
```

## Configuration Profiles

### Active Profiles

Set active profile:

```yaml
spring:
  profiles:
    active: dev
```

### Profile-Specific Configuration

Files are loaded in order:

1. `application.yml` (default)
2. `application-{profile}.yml` (profile-specific)
3. `{application}.yml` (application default)
4. `{application}-{profile}.yml` (application profile-specific)

Example for `auth-service` with `prod` profile:

1. `application.yml`
2. `application-prod.yml`
3. `auth-service.yml`
4. `auth-service-prod.yml`

## Running the Service

### Local Development

```bash
cd config-server
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd config-server && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t config-server:latest .

# Run container
docker run -p 8888:8888 config-server:latest
```

### Git Repository Setup

Create a local Git repository:

```bash
mkdir -p ~/config-repo/configs
cd ~/config-repo
git init
cp ~/ecommerce/auth-service/src/main/resources/application.yml configs/auth-service.yml
git add .
git commit -m "Initial configuration"
```

Update Config Server to use repository:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: file:///home/user/config-repo
```

## Testing

### Verify Configuration Server

1. **Start Config Server**:
   ```bash
   cd config-server && mvn spring-boot:run
   ```

2. **Test Configuration Access**:
   ```bash
   curl http://localhost:8888/auth-service/default
   ```

3. **Verify Service Configuration**:
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

4. **Check Service Logs**:
   - Look for "Fetching config from server" message
   - Verify configuration is loaded

### Test Configuration Refresh

1. **Update Configuration** in Git repository
2. **Commit Changes**:
   ```bash
   cd ~/config-repo
   vi configs/auth-service.yml
   git commit -am "Update config"
   ```
3. **Refresh Configuration**:
   ```bash
   curl -X POST http://localhost:8080/actuator/refresh
   ```
4. **Verify Changes** are applied

## Deployment Considerations

### Production Configuration

1. **Secure Git Repository**:
   - Use SSH authentication
   - Use environment variables for credentials
   - Enable HTTPS for Git operations

2. **Configuration Backup**:
   - Version control all configurations
   - Implement configuration rollback
   - Maintain configuration history

3. **High Availability**:
   - Deploy multiple Config Server instances
   - Use load balancer for Config Server
   - Implement failover mechanism

4. **Security**:
   - Encrypt sensitive properties
   - Use secure key management
   - Restrict access to Config Server
   - Enable HTTPS

### Configuration Management

1. **Environment Separation**:
   - Keep configurations separate by environment
   - Use clear naming conventions
   - Document configuration differences

2. **Validation**:
   - Validate configuration before deployment
   - Use schema validation where possible
   - Test configuration in staging first

3. **Monitoring**:
   - Monitor Config Server health
   - Track configuration changes
   - Set up alerts for configuration errors

## Troubleshooting

### Configuration Not Loading

1. **Check Config Server**: Ensure it's running at http://localhost:8888
2. **Check Service Configuration**: Verify `spring.config.import` is correct
3. **Check Git Repository**: Ensure configuration files exist
4. **Check Network**: Verify service can reach Config Server
5. **Check Logs**: Review service logs for configuration errors

### Configuration Changes Not Applied

1. **Refresh Configuration**: Call `/actuator/refresh` endpoint
2. **Check Git Repository**: Ensure changes are committed
3. **Check Profile**: Verify correct profile is active
4. **Check Property Source**: Verify property source priority
5. **Restart Service**: Try restarting the service

### Encryption/Decryption Errors

1. **Check Key**: Ensure encryption key is correct
2. **Check Key Store**: Verify keystore file exists and is accessible
3. **Check Password**: Verify keystore password is correct
4. **Check Format**: Ensure encrypted values use correct format: `{cipher}...`

### Git Repository Access Issues

1. **Check URI**: Verify Git repository URI is correct
2. **Check Authentication**: Ensure credentials are valid (if required)
3. **Check Permissions**: Verify Config Server has read access
4. **Check Network**: Ensure Config Server can reach Git repository
5. **Check Branch**: Verify default branch exists

## Best Practices

### 1. Configuration Organization

- Group related configurations together
- Use clear, descriptive naming
- Separate sensitive and non-sensitive data
- Document configuration options

### 2. Security

- Encrypt sensitive properties (passwords, API keys)
- Use environment variables for secrets
- Never commit secrets to version control
- Use secure key management systems

### 3. Version Control

- Track all configuration changes
- Use meaningful commit messages
- Tag configuration releases
- Maintain configuration history

### 4. Testing

- Test configuration changes in staging
- Validate configuration before deployment
- Monitor services after configuration changes
- Have rollback plan ready

## Future Improvements

- [ ] Implement configuration validation
- [ ] Add configuration diff tool
- [ ] Implement configuration versioning UI
- [ ] Add configuration change notifications
- [ ] Implement configuration rollback
- [ ] Add configuration templates
- [ ] Implement configuration documentation generator
- [ ] Add configuration audit logging

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

## Additional Resources

- [Spring Cloud Config Documentation](https://cloud.spring.io/spring-cloud-config/reference/html/)
- [Spring Cloud Config Samples](https://github.com/spring-cloud-samples/config-repo)

---

For more information, see: [main project README](../README.md).
