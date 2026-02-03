package com.fortune.order.config;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient("inventory-service")
public interface InventoryClient {
    @PostMapping("/api/v1/inventory/deduct")
    public ResponseEntity<String>deductInventory(@RequestBody Map<String,Object> body);
}
