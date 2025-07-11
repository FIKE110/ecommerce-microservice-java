package com.fortune.cart.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("product-service")
public interface ProductClient {

    @GetMapping("/api/v1/product/{id}")
    String getProducts(@RequestHeader("Authorization") String token, @PathVariable(value = "id") UUID id);

}
