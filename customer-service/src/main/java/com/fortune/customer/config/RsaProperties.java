package com.fortune.customer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;


@ConfigurationProperties("rsa")
@Setter
@Getter
public class RsaProperties {

    private RSAPublicKey publicKey;
}
