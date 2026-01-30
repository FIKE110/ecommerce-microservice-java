# Utils

Shared utilities and common code module used by all microservices.

## Overview

The Utils module provides common utilities, DTOs, exceptions, and helper functions used across all microservices. This module is included as a dependency in all services to avoid code duplication and ensure consistency.

## Features

- Common API response wrappers
- Shared DTOs (Data Transfer Objects)
- Common exception classes
- Utility functions
- Shared constants and enums
- Common validation helpers
- Date/time utilities
- String manipulation utilities

## Tech Stack

- **Language**: Java 17
- **Build Tool**: Maven
- **Validation**: Jakarta Bean Validation

## Project Structure

```
utils/
├── src/main/java/com/fortune/
│   ├── ApiResponse.java          # API response wrappers
│   ├── ApiDataResponse.java      # Data response wrapper
│   ├── DataWrapper.java          # Generic data wrapper
│   ├── MessageInString.java      # String message wrapper
│   ├── exception/               # Common exceptions
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ValidationException.java
│   ├── util/                   # Utility classes
│   │   ├── DateUtils.java
│   │   ├── StringUtils.java
│   │   └── ValidationUtils.java
│   └── constant/               # Shared constants
│       ├── AppConstants.java
│       └── ErrorCodes.java
└── pom.xml
```

## Common Classes

### API Response Wrappers

#### ApiResponse

```java
public class ApiResponse {
    private String code;
    private String message;

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode("SUCCESS");
        response.setMessage(message);
        return response;
    }
}
```

#### ApiDataResponse

```java
public class ApiDataResponse<T> {
    private String code;
    private T data;

    public static <T> ApiDataResponse<T> data(String code, T data) {
        ApiDataResponse<T> response = new ApiDataResponse<>();
        response.setCode(code);
        response.setData(data);
        return response;
    }

    public static <T> ApiDataResponse<T> error(String code, String message) {
        ApiDataResponse<T> response = new ApiDataResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
```

#### DataWrapper

```java
public class DataWrapper<T, R> {
    private T code;
    private R data;

    public DataWrapper(T code, R data) {
        this.code = code;
        this.data = data;
    }

    public T getCode() {
        return code;
    }

    public R getData() {
        return data;
    }
}
```

#### MessageInString

```java
public class MessageInString {
    private String message;

    public MessageInString(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

### Usage Examples

#### Service Response

```java
@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<DataWrapper<ResponseCode, ExampleDto>>> getExample(
        @PathVariable UUID id
    ) {
        ExampleDto example = exampleService.findById(id);

        return ResponseEntity.ok(
            ApiResponse.data(
                ResponseCode.EXAMPLE_FETCHED,
                example
            )
        );
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<DataWrapper<ResponseCode, MessageInString>>> createExample(
        @RequestBody @Valid ExampleRequest request
    ) {
        exampleService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.data(
                ResponseCode.EXAMPLE_CREATED,
                new MessageInString("Example created successfully")
            )
        );
    }
}
```

## Common Exceptions

### ResourceNotFoundException

```java
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String field;
    private final Object value;

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s : '%s'", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    // Getters...
}
```

### ValidationException

```java
public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
```

## Utility Classes

### DateUtils

```java
public class DateUtils {

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2);
    }

    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2);
    }
}
```

### StringUtils

```java
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength
            ? str.substring(0, maxLength)
            : str;
    }

    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
```

### ValidationUtils

```java
public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@(.+\\.)+.+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        String regex = "^[+]?[0-9]{10,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isValidUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

## Shared Constants

### AppConstants

```java
public class AppConstants {

    // Service Names
    public static final String AUTH_SERVICE = "auth-service";
    public static final String PRODUCT_SERVICE = "product-service";
    public static final String ORDER_SERVICE = "order-service";
    public static final String CART_SERVICE = "cart-service";
    public static final String CUSTOMER_SERVICE = "customer-service";
    public static final String INVENTORY_SERVICE = "inventory-service";
    public static final String PAYMENT_SERVICE = "payment-service";
    public static final String NOTIFICATION_SERVICE = "notification-service";

    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Pagination
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Cache
    public static final int CACHE_EXPIRY_MINUTES = 30;

    // JWT
    public static final int TOKEN_EXPIRY_MINUTES = 60;
    public static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
}
```

### ErrorCodes

```java
public class ErrorCodes {

    // Generic Errors
    public static final String SUCCESS = "SUCCESS";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String NOT_FOUND = "NOT_FOUND";

    // Resource Errors
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";

    // Authentication Errors
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
}
```

## Usage in Services

### Adding Dependency

Add utils module as a dependency in service's `pom.xml`:

```xml
<dependency>
    <groupId>com.fortune</groupId>
    <artifactId>utils</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Using Shared Classes

```java
import com.fortune.ApiResponse;
import com.fortune.ApiDataResponse;
import com.fortune.DataWrapper;
import com.fortune.MessageInString;
import com.fortune.exception.ResourceNotFoundException;
import com.fortune.util.DateUtils;
import com.fortune.constant.AppConstants;

@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<DataWrapper<ResponseCode, ExampleDto>>> getExample(
        @PathVariable UUID id
    ) {
        try {
            ExampleDto example = exampleService.findById(id);

            return ResponseEntity.ok(
                ApiResponse.data(
                    ResponseCode.EXAMPLE_FETCHED,
                    example
                )
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                    ErrorCodes.RESOURCE_NOT_FOUND,
                    e.getMessage()
                )
            );
        }
    }
}
```

## Global Exception Handler

### Custom Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.error(ErrorCodes.RESOURCE_NOT_FOUND, ex.getMessage())
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse.error(ErrorCodes.VALIDATION_ERROR, "Validation failed")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse.error(ErrorCodes.VALIDATION_ERROR, "Validation failed")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.error(ErrorCodes.INTERNAL_ERROR, "An error occurred")
        );
    }
}
```

## Testing

### Run Tests

```bash
cd utils
mvn test
```

### Test Coverage

- Unit tests for utility classes
- Exception handler tests
- Validation tests
- Date/time utilities tests

## Best Practices

### 1. Keep Utilities Generic

- Avoid business logic in utils
- Make utilities reusable
- Keep dependencies minimal
- Use generic types where appropriate

### 2. Documentation

- Document all public methods
- Include usage examples
- Explain edge cases
- Document parameters and return values

### 3. Immutability

- Make utility methods stateless
- Avoid modifying input parameters
- Return new objects instead of modifying
- Use immutable collections

### 4. Error Handling

- Use checked exceptions for recoverable errors
- Use unchecked exceptions for programming errors
- Provide meaningful error messages
- Include context in exceptions

## Future Improvements

- [ ] Add logging utilities
- [ ] Implement more validation helpers
- [ ] Add encryption/decryption utilities
- [ ] Implement more date/time utilities
- [ ] Add JSON serialization utilities
- [ ] Implement retry mechanism utilities
- [ ] Add pagination helper classes
- [ ] Implement more string manipulation utilities

## Dependencies

See `pom.xml` for full list of dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <!-- Other common dependencies -->
</dependencies>
```

## Additional Resources

- [Jakarta Bean Validation Documentation](https://beanvalidation.org/)
- [Java Date/Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html)

---

For more information, see: [main project README](../README.md).
