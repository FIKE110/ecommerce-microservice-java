package com.fortune.auth.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record CustomerSignupRequest(
        @Email
        String email,
        @NotEmpty
        @Length(min = 6)
        String password,
        @NotBlank
        String username,
        @NotBlank
        String phoneNumber
) {
}
