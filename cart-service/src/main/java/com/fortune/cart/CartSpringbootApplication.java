package com.fortune.cart;

import com.fortune.cart.config.RsaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(RsaProperties.class)
public class CartSpringbootApplication{
    public static void main(String[] args) {
        SpringApplication.run(CartSpringbootApplication.class, args);
    }
}