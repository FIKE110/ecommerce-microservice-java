package com.fortune.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerSigninRequest (
        @Email
        String email,
        @NotBlank
        String password
){
}
