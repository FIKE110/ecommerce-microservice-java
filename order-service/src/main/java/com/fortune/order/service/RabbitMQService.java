package com.fortune.order.service;


import com.fortune.Event;
import com.fortune.EventType;
import com.fortune.order.config.InventoryClient;
import com.fortune.order.config.RabbitMQConfig;
import com.fortune.order.enumeration.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RabbitMQService {
    private final String QUEUE_NAME = "order";
    private final OrderService orderService;
    private final InventoryClient inventoryClient;

    public RabbitMQService(OrderService orderService, InventoryClient inventoryClient) {
        this.orderService = orderService;
        this.inventoryClient = inventoryClient;
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(Event event) {
        if(event.getEventType().equals(EventType.ORDER_PAID)){
            Map<String,String> message = (Map<String, String>) event.getMessage();
            String reference=message.get("reference_number");
            orderService.updateOrderStatusByReference(reference, OrderStatus.PAID);
            orderService.findByReference(reference).ifPresent(order -> {
                orderService.updateOrderShipingAndDelivery(order);
               order.getProducts().forEach(product -> {
                   inventoryClient.deductInventory(Map.of(
                          "productId",product.getProductId(),
                           "quantity",product.getQuantity()
                   ));
               }) ;
            });
        }
        else if(event.getEventType().equals(EventType.ORDER_FAIL)){
            Map<String,String> message = (Map<String, String>) event.getMessage();
            String reference=message.get("reference_number");
            orderService.updateOrderStatusByReference(reference, OrderStatus.FAILED);
        }
    }

}
