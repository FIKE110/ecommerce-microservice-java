package com.fortune.inventory.repository;

import com.fortune.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory,Long>, PagingAndSortingRepository<Inventory,Long> {
    Optional<Inventory> findInventoryByProductId(UUID productId);
}
