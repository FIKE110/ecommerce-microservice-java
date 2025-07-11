package com.fortune.auth.request;

import jakarta.validation.constraints.NotBlank;

public record CustomerRefreshTokenRequest(
        @NotBlank
        String refreshToken
) {
}
