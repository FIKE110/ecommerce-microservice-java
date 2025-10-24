package com.fortune.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "app")
public record AppConfigProp (
    String title,
    String description,
    String version,
    String url
){}
