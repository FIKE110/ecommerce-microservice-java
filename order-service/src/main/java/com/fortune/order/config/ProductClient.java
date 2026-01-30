package com.fortune.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="product-service")
public interface ProductClient {
    @GetMapping("/api/v1/product/{id}/name")
    public ResponseEntity<Map<String,String>> getProductName(@RequestHeader("Authorization") String token,@PathVariable("id") String id);
}
