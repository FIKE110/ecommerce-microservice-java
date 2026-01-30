# Notification Service

Notification management microservice handling email, SMS, and push notifications for e-commerce platform.

## Overview

The Notification Service manages all customer communications including order confirmations, shipping updates, promotional emails, and SMS notifications. It provides APIs for sending notifications and integrates with external email and SMS providers.

## Features

- Email notifications (transactional and promotional)
- SMS notifications
- Order status updates
- Promotional campaigns
- Notification templates
- Delivery tracking
- Integration with RabbitMQ for event-driven notifications

## Tech Stack

- **Framework**: Spring Boot 3.5.3
- **Email**: JavaMail API
- **SMS**: Twilio (or other provider)
- **Template Engine**: Thymeleaf
- **Service Discovery**: Netflix Eureka
- **Message Queue**: RabbitMQ
- **Tracing**: Zipkin + Brave

## API Documentation

**Swagger UI**: http://localhost:8086/api/v1/notification/docs
**OpenAPI JSON**: http://localhost:8086/api/v1/notification/v3/api-docs

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: notification-service
  mail:
    host: smtp.example.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

twilio:
  accountSid: ${TWILIO_ACCOUNT_SID}
  authToken: ${TWILIO_AUTH_TOKEN}
  fromNumber: ${TWILIO_FROM_NUMBER}

api:
  url: '/api/v1/notification'

server:
  port: 0  # Random port assigned by Eureka
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `EMAIL_HOST` | SMTP server host | Yes |
| `EMAIL_PORT` | SMTP server port | Yes |
| `EMAIL_USERNAME` | SMTP username | Yes |
| `EMAIL_PASSWORD` | SMTP password | Yes |
| `TWILIO_ACCOUNT_SID` | Twilio account SID | Optional |
| `TWILIO_AUTH_TOKEN` | Twilio auth token | Optional |
| `TWILIO_FROM_NUMBER` | Twilio phone number | Optional |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | No |

## API Endpoints

### Email Notification Endpoints

#### Send Email
Send an email notification.

```http
POST /api/v1/notification/email
Content-Type: application/json

{
  "to": "customer@example.com",
  "subject": "Order Confirmation",
  "template": "order-confirmation",
  "data": {
    "orderId": "12345",
    "customerName": "John Doe",
    "totalAmount": "$100.00",
    "orderItems": [
      {
        "name": "Product 1",
        "quantity": 2,
        "price": "$25.00"
      }
    ]
  }
}
```

**Response**: `200 OK`

```json
{
  "notificationId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SENT",
  "message": "Email sent successfully"
}
```

#### Send Bulk Email
Send email to multiple recipients.

```http
POST /api/v1/notification/email/bulk
Content-Type: application/json

{
  "recipients": ["customer1@example.com", "customer2@example.com"],
  "subject": "Special Offer!",
  "template": "promotion",
  "data": {
    "promoCode": "SAVE20",
    "validUntil": "2024-12-31"
  }
}
```

**Response**: `200 OK`

```json
{
  "notificationId": "660e9500-e39b-51d4-a816-556655550000",
  "recipients": 2,
  "status": "SENT",
  "message": "Bulk email sent successfully"
}
```

### SMS Notification Endpoints

#### Send SMS
Send an SMS notification.

```http
POST /api/v1/notification/sms
Content-Type: application/json

{
  "to": "+1234567890",
  "message": "Your order #12345 has been shipped!"
}
```

**Response**: `200 OK`

```json
{
  "notificationId": "770e0600-e49b-61d4-b916-666656560000",
  "status": "SENT",
  "message": "SMS sent successfully"
}
```

### Notification Status Endpoints

#### Get Notification Status
Check delivery status of a notification.

```http
GET /api/v1/notification/status/{notificationId}
```

**Response**: `200 OK`

```json
{
  "notificationId": "550e8400-e29b-41d4-a716-446655440000",
  "type": "EMAIL",
  "status": "DELIVERED",
  "sentAt": "2024-01-15T10:00:00Z",
  "deliveredAt": "2024-01-15T10:00:05Z",
  "retryCount": 0
}
```

#### Get Notification History
Get notification history for a user.

```http
GET /api/v1/notification/history?email=customer@example.com&page=0&size=10
```

**Response**: `200 OK`

```json
{
  "content": [
    {
      "notificationId": "550e8400-e29b-41d4-a716-446655440000",
      "type": "EMAIL",
      "subject": "Order Confirmation",
      "status": "DELIVERED",
      "sentAt": "2024-01-15T10:00:00Z"
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

## Data Model

### Notification Entity

```java
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private UUID id;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String template;
    private NotificationStatus status;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String errorMessage;

    @Lob
    private String content;
}
```

### NotificationType Enum

```java
public enum NotificationType {
    EMAIL,
    SMS,
    PUSH
}
```

### NotificationStatus Enum

```java
public enum NotificationStatus {
    PENDING,      // Notification created, waiting to send
    SENT,         // Notification sent successfully
    DELIVERED,    // Notification delivered to recipient
    FAILED,       // Notification failed
    RETRYING      // Retrying to send notification
}
```

## Email Templates

### Template Location

Templates are located in `src/main/resources/templates/`:

```
templates/
├── order-confirmation.html
├── order-shipped.html
├── order-delivered.html
├── payment-successful.html
├── password-reset.html
├── welcome-email.html
└── promotion.html
```

### Example Template: Order Confirmation

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Order Confirmation</title>
</head>
<body>
    <h1>Order Confirmation</h1>
    <p>Dear <span th:text="${customerName}">Customer</span>,</p>
    <p>Your order #<span th:text="${orderId}">12345</span> has been confirmed!</p>
    <table>
        <tr th:each="item : ${orderItems}">
            <td th:text="${item.name}">Product</td>
            <td th:text="${item.quantity}">1</td>
            <td th:text="${item.price}">$0.00</td>
        </tr>
    </table>
    <p>Total: <span th:text="${totalAmount}">$0.00</span></p>
</body>
</html>
```

## RabbitMQ Integration

### Event Consumption

```java
@Component
public class NotificationConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderEvent event) {
        switch (event.getType()) {
            case ORDER_CONFIRMED:
                sendOrderConfirmation(event);
                break;
            case ORDER_SHIPPED:
                sendShippingNotification(event);
                break;
            case ORDER_DELIVERED:
                sendDeliveryNotification(event);
                break;
            case PAYMENT_SUCCESSFUL:
                sendPaymentConfirmation(event);
                break;
        }
    }
}
```

### Event Types

- `ORDER_CONFIRMED`: Send order confirmation email
- `ORDER_SHIPPED`: Send shipping notification (email + SMS)
- `ORDER_DELIVERED`: Send delivery confirmation
- `PAYMENT_SUCCESSFUL`: Send payment confirmation
- `USER_REGISTERED`: Send welcome email
- `PASSWORD_RESET`: Send password reset email

## Email Configuration

### JavaMail Configuration

```java
@Configuration
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(getMailProperties());
        return mailSender;
    }

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }
}
```

### Email Sending Service

```java
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(EmailRequest request) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(getContent(request), true);

        mailSender.send(message);
    }

    private String getContent(EmailRequest request) {
        Context context = new Context();
        context.setVariables(request.getData());
        return templateEngine.process(
            request.getTemplate(),
            context
        );
    }
}
```

## SMS Configuration

### Twilio Configuration

```java
@Configuration
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Bean
    public TwilioRestClient twilioRestClient() {
        return new TwilioRestClient.Builder(accountSid, authToken).build();
    }
}
```

### SMS Sending Service

```java
@Service
public class SmsService {

    private final TwilioRestClient twilioRestClient;

    @Value("${twilio.fromNumber}")
    private String fromNumber;

    public void sendSms(String to, String message) {
        Message.creator(
            new PhoneNumber(to),
            new PhoneNumber(fromNumber),
            message
        ).create(twilioRestClient);
    }
}
```

## Service Interactions

The Notification Service communicates with:

- **Order Service**: Listens for order status changes
- **Payment Service**: Listens for payment events
- **Auth Service**: Listens for user registration events
- **Customer Service**: Uses customer email/phone for notifications

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
| `NOTIFICATION_NOT_FOUND` | 404 | Notification does not exist |
| `INVALID_RECIPIENT` | 400 | Invalid email address or phone number |
| `SEND_FAILED` | 500 | Failed to send notification |
| `TEMPLATE_NOT_FOUND` | 404 | Email template does not exist |
| `VALIDATION_ERROR` | 400 | Request validation failed |

### Retry Mechanism

Failed notifications are automatically retried:

```java
@Retryable(
    value = { MailSendException.class },
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public void sendEmailWithRetry(EmailRequest request) {
    sendEmail(request);
}
```

## Running Service

### Local Development

```bash
cd notification-service
mvn spring-boot:run
```

### From Root Directory

```bash
# Using Make
make start-backend

# Or manually
cd notification-service && mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t notification-service:latest .

# Run container
docker run -p 8086:8086 notification-service:latest
```

## Testing

### Run Tests

```bash
cd notification-service
mvn test
```

### Test Coverage

- Unit tests for EmailService
- Unit tests for SmsService
- Integration tests for NotificationController
- Template rendering tests
- Event consumption tests

### Manual Testing

```bash
# Send test email
curl -X POST http://localhost:8086/api/v1/notification/email \
  -H "Content-Type: application/json" \
  -d '{
    "to": "test@example.com",
    "subject": "Test Email",
    "template": "welcome-email",
    "data": {"customerName": "Test User"}
  }'
```

## Monitoring

### Actuator Endpoints

- **Health**: http://localhost:8086/actuator/health
- **Metrics**: http://localhost:8086/actuator/metrics
- **Info**: http://localhost:8086/actuator/info

### Distributed Tracing

Traces are sent to Zipkin at `http://localhost:9411/api/v2/spans`

## Performance Considerations

### Email Sending

- Use asynchronous sending for bulk emails
- Implement rate limiting to avoid spam filters
- Use connection pooling for SMTP
- Monitor delivery rates and bounce rates

### SMS Sending

- Implement rate limiting for SMS
- Use bulk SMS API for multiple recipients
- Monitor SMS costs and delivery rates
- Implement SMS scheduling for off-peak hours

## Deployment Considerations

### Production Configuration

1. **Email Provider**: Use transactional email service (SendGrid, AWS SES, etc.)
2. **SMS Provider**: Configure production SMS provider
3. **Queue Management**: Use RabbitMQ for reliable message delivery
4. **Monitoring**: Set up alerts for failed notifications
5. **Backup Provider**: Configure backup email/SMS provider

### Scaling

- Deploy multiple instances behind load balancer
- Use RabbitMQ for distributed queue
- Implement notification queues by type (email, SMS)
- Consider regional deployment for reduced latency

## Troubleshooting

### Emails Not Sending

1. Verify SMTP credentials
2. Check SMTP server connectivity
3. Review email service logs
4. Verify recipient email address
5. Check spam filter settings

### SMS Not Sending

1. Verify Twilio credentials
2. Check account balance
3. Review Twilio error logs
4. Verify phone number format
5. Check country code

### Templates Not Rendering

1. Verify template file exists
2. Check template syntax (Thymeleaf)
3. Review template variables
4. Check template engine configuration
5. Test template manually

## Best Practices

### Email Best Practices

1. Use double opt-in for promotional emails
2. Include unsubscribe links
3. Test emails across multiple email clients
4. Use responsive email design
5. Avoid spam trigger words

### SMS Best Practices

1. Get explicit opt-in consent
2. Include opt-out mechanism
3. Keep messages concise
4. Send during business hours (local time)
5. Monitor SMS delivery rates

## Future Improvements

- [ ] Implement push notifications (mobile app)
- [ ] Add email analytics (open rates, click rates)
- [ ] Implement A/B testing for templates
- [ ] Add notification preferences (user opt-in/opt-out)
- [ ] Implement notification scheduling
- [ ] Add rich media notifications (attachments, images)
- [ ] Implement SMS two-factor authentication
- [ ] Add notification templates management UI

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    <!-- Twilio (if used) -->
    <!-- ... -->
</dependencies>
```

## Additional Resources

- [JavaMail API Documentation](https://javaee.github.io/javamail/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Twilio Documentation](https://www.twilio.com/docs)
- [Spring Email Documentation](https://docs.spring.io/spring-framework/reference/integration/mail.html)

---

For more information, see: [main project README](../README.md).
