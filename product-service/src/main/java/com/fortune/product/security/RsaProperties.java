package com.fortune.product.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties("rsa")
public record RsaProperties(
        RSAPublicKey publicKey
) {
}
