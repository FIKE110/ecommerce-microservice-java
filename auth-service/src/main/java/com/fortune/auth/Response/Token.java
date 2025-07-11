package com.fortune.auth.Response;

public record Token(
        String accessToken,
        String refreshToken
) {
}
