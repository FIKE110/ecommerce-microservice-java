package com.fortune.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;


@FeignClient(name="inventory-service")
public interface InventoryClient {
    @GetMapping("/api/v1/inventory/product/{id}")
    public ResponseEntity<Map<String,String>> getProductInventory(@PathVariable("id") UUID id);
}
