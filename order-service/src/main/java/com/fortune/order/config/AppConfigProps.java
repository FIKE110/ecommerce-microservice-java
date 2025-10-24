package com.fortune.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppConfigProps (
        String title,
        String description,
        String version,
        String url
){}
