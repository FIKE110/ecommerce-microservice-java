package com.fortune.cart;

import com.fortune.cart.config.RsaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(RsaProperties.class)
@EnableFeignClients
public class CartSpringbootApplication{
    public static void main(String[] args) {
        SpringApplication.run(CartSpringbootApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestClient restClient() {
        return RestClient
                .builder()
                .build();
    }

}