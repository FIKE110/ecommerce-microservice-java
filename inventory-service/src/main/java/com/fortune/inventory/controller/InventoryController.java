package com.fortune.inventory.controller;

import com.fortune.inventory.entity.Inventory;
import com.fortune.inventory.request.InventoryRequest;
import com.fortune.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"${api.url}/","${api.url}"})
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<Page<Inventory>> getInventory(@RequestParam(value = "page",defaultValue = "0") int page,
                                                        @RequestParam(value = "size",defaultValue = "1" )int size,
                                                        @RequestParam(value = "sort", defaultValue = "updatedAt") String sort
                             ) {
        return ResponseEntity.ok(
          inventoryService.findAll(page, size, sort));
    }

    @GetMapping("{id}")
    public ResponseEntity<Inventory> getInventory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<Inventory>> getInStock(){
        return ResponseEntity.ok(inventoryService.stillInStock());
    }

    @GetMapping("/out-stock")
    public ResponseEntity<List<Inventory>> getOutOfStock(){
        return ResponseEntity.ok(inventoryService.outOfStock());
    }

    @PostMapping
    public ResponseEntity<String> createInventory(@RequestBody @Valid InventoryRequest inventoryRequest) {
        inventoryService.addToStock(inventoryRequest.productId(),inventoryRequest.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductInventory(@RequestBody @Valid InventoryRequest inventoryRequest) {
        inventoryService.removeFromStock(inventoryRequest.productId(),inventoryRequest.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
