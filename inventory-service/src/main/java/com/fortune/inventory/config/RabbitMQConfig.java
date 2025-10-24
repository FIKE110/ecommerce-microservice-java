package com.fortune.inventory.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "inventory";
    public static final String FANOUT_QUEUE_NAME = "inventory-fanout";
    private static String EXCHANGE_NAME = "inventory_exchange";
    private  static String ROUTING_KEY = "inventory";
    public static final String FANOUT_EXCHANGE = "cart-fanout-exchange";

    @Primary
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    @Qualifier("fanoutQueue")
    public Queue fanoutQueue() {
        return new Queue(FANOUT_QUEUE_NAME);
    }

    @Primary
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    @Qualifier("fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Primary
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding fanoutBinding(FanoutExchange fanoutExchange, @Qualifier("fanoutQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(fanoutExchange)
                ;
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

}
