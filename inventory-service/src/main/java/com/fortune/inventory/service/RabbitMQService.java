package com.fortune.inventory.service;

import com.fortune.Event;
import com.fortune.EventType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RabbitMQService {
    private final String QUEUE_NAME = "inventory";
    private final InventoryService inventoryService;

    public RabbitMQService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(Event event) {
        if(event.getEventType().equals(EventType.PRODUCT_CREATED)){
            Map<String,String> message = event.getMessage();
            UUID productId = UUID.fromString(message.get("id"));
            Long quantity = Long.parseLong(message.get("quantity"));
            inventoryService.intializeStock(productId,quantity);
        }
    }
}
