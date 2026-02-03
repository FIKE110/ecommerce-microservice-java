package com.fortune.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@FeignClient(name="customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customer")
    public ResponseEntity<Map<String,String>> getCustomerProfile(@RequestHeader("Authorization") String token) ;
}
