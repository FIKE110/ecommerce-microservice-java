package com.fortune.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/api/v1/auth/profile")
    public ResponseEntity<Map<String,String>> profile(@RequestParam("username") String username);
}
