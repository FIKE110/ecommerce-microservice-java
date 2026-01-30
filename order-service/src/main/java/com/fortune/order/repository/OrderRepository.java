package com.fortune.order.repository;

import com.fortune.order.enumeration.OrderStatus;
import com.fortune.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, PagingAndSortingRepository<Order, UUID> {
    Page<Order> findByUsername(String username, Pageable pageable);

    Optional<Order> findByTxnReference(String txnReference);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}