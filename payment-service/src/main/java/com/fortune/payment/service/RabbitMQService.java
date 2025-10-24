package com.fortune.payment.service;

import com.fortune.Event;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    //private final String QUEUE_NAME = "inventory";
    //private final String EXCHANGE_NAME = "inventory_exchange";
    private final String ROUTING_KEY = "order";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Event message) {
        rabbitTemplate.convertAndSend(ROUTING_KEY,message);
    }
}
