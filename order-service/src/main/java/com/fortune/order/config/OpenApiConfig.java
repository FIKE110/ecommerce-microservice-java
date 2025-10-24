package com.fortune.order.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(AppConfigProps appConfigProps) {
        return new OpenAPI()
                .info(new Info()
                        .title(appConfigProps.title())
                        .version(appConfigProps.version())
                        .description(appConfigProps.description())
                )
                .servers(List.of(
                        new Server().url("/") // 👈 custom base URL
                ));
    }
}

