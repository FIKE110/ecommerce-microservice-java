package com.fortune.cart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

@ConfigurationProperties("rsa")
public record RsaProperties(
        RSAPublicKey publicKey
) {

}
