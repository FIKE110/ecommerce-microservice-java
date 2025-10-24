package com.fortune.cart.config;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderClient {
    @PostMapping("/api/v1/order")
    public ResponseEntity<Map<String,String>> createOrder(@RequestHeader("Authorization") String token, @RequestParam ("username") String username , @RequestBody Map<String,Map<String,Double>> order);
}
