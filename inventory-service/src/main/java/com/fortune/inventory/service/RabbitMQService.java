package com.fortune.inventory.service;

import com.fortune.utils.Event;
import com.fortune.utils.EventType;
import com.fortune.inventory.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

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
            Map<String,String> message = (Map<String, String>) event.getMessage();
            UUID productId = UUID.fromString(message.get("id"));
            Long quantity = Long.parseLong(message.get("quantity"));
            inventoryService.intializeStock(productId,quantity);
        }
        else if (event.getEventType().equals(EventType.ORDER_PAID)) {
            Map<String, String> message = (Map<String, String>) event.getMessage();
            for (Map.Entry<String, String> entry : message.entrySet()) {
                String productId = entry.getKey();
                Long quantity = Long.valueOf(entry.getValue());
                inventoryService.removeFromStock(UUID.fromString(productId), quantity);
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveFanoutMessage(Event event) {
       System.out.println(event);
    }
}
