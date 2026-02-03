package com.fortune.order.service;


import com.fortune.order.enumeration.OrderStatus;
import com.fortune.order.model.Order;
import com.fortune.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderTrackerService {


    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderTrackerService(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    private static final int PAGE_SIZE = 50;

    @Scheduled(fixedRate = 60_000)
    public void updateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();

        shipPaidOrders(now);
        deliverShippedOrders(now);
    }

    private void shipPaidOrders(LocalDateTime now) {

        Page<Order> orderPage = orderRepository.findByStatus(
                OrderStatus.PAID,
                PageRequest.of(0, PAGE_SIZE)
        );

        if (orderPage.isEmpty()) return;

        List<Order> toShip = orderPage.getContent().stream()
                .filter(order ->
                        order.getShippingTime() != null &&
                                !order.getShippingTime().isAfter(now)
                )
                .peek(order -> order.setStatus(OrderStatus.SHIPPED))
                .toList();

        if (!toShip.isEmpty()) {
            orderRepository.saveAll(toShip);
        }
    }




    private void deliverShippedOrders(LocalDateTime now) {

        Page<Order> orderPage = orderRepository.findByStatus(
                OrderStatus.SHIPPED,
                PageRequest.of(0, PAGE_SIZE)
        );

        if (orderPage.isEmpty()) {
            return;
        }

        List<Order> toDeliver = orderPage.getContent().stream()
                .filter(order ->
                        order.getDeliveryTime() != null &&
                                !order.getDeliveryTime().isAfter(now)
                )
                .peek(order -> order.setStatus(OrderStatus.DELIVERED))
                .toList();

        if (!toDeliver.isEmpty()) {
            orderRepository.saveAll(toDeliver);
        }
    }


}
