package com.fortune.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app")
public record AppConfigProp (
    String title,
    String description,
    String version,
    String url
){}
