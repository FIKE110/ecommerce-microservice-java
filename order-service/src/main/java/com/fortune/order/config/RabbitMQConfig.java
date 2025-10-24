package com.fortune.order.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "order";
    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String FANOUT_EXCHANGE = "cart-fanout-exchange";
    public static final String ORDER_ROUTING_KEY = "order";
    private ConnectionFactory connectionFactory;


    public RabbitMQConfig() {

    }


    @Bean
    public Queue fanoutQueue() {
        return new Queue(ORDER_QUEUE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding(FanoutExchange fanoutExchange, Queue fanoutQueue) {
        return BindingBuilder
                .bind(fanoutQueue)
                .to(fanoutExchange);

    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }


}
