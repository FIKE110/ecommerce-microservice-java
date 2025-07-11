package com.fortune.auth.request;

import jakarta.validation.constraints.Email;

public record CustomerVerifyEmailRequest (
        @Email
        String email
){
}
