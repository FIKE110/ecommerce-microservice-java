package com.fortune.inventory.service;

import com.fortune.inventory.entity.Inventory;
import com.fortune.inventory.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Page<Inventory> findAll(int page, int size,String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return inventoryRepository.findAll(pageable);
    }

    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(()->new RuntimeException("Inventory not found"));
    }

    public List<Inventory> outOfStock() {
        return inventoryRepository.findAll().stream().filter(inventory -> inventory.getQuantity()<0)
                .collect(Collectors.toSet()).stream().toList();
    }

    public List<Inventory> stillInStock() {
        return inventoryRepository.findAll().stream().filter(inventory -> inventory.getQuantity()>0)
                .collect(Collectors.toSet()).stream().toList();
    }


        public void addToStock(UUID productId, Long quantity) {
        Inventory inventory=inventoryRepository.findInventoryByProductId(productId).orElseThrow(()-> new RuntimeException("Product not found"));
        inventory.setProductId(productId);
        inventory.setQuantity(inventory.getQuantity()+quantity);
        inventoryRepository.save(inventory);
    }

    public void removeFromStock(UUID productId, Long quantity) {
        Inventory inventory=inventoryRepository.findInventoryByProductId(productId).orElseThrow(()-> new RuntimeException("Product not found"));
        if(inventory.getQuantity()-quantity<0) return;
        inventory.setProductId(productId);
        inventory.setQuantity(inventory.getQuantity()-quantity);
        inventoryRepository.save(inventory);
    }

    public void intializeStock(UUID productId, Long quantity) {
        Inventory inventory=new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }

}
