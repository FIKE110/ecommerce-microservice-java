package com.fortune.inventory;

import com.fortune.inventory.config.AppConfigProp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({AppConfigProp.class})
@SpringBootApplication
public class InventorySpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventorySpringApplication.class, args);
    }
}
