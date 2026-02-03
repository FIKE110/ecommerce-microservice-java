package com.fortune.order;

import com.fortune.order.config.AppConfigProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({AppConfigProps.class})
@EnableFeignClients
@EnableScheduling
public class OrderSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSpringApplication.class, args);
    }
}
