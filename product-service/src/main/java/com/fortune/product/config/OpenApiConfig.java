package com.fortune.product.config;

import com.fortune.product.config.AppConfigProp;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(AppConfigProp appConfigProp) {
        return new OpenAPI()
                .info(new Info()
                        .title(appConfigProp.title())
                        .version(appConfigProp.version())
                        .description(appConfigProp.description())
                )
                .servers(List.of(
                        new Server().url("/") // 👈 custom base URL
                ));
    }
}

