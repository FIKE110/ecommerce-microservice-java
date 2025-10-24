package com.fortune.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.order.config.PaymentClient;
import com.fortune.order.model.Order;
import com.fortune.order.model.PaymentResponseCheckout;
import com.fortune.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"${api.url}","${api.url}/"})
public class OrderController {

    private final OrderService orderService;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;

    public OrderController(OrderService orderService, PaymentClient paymentClient, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.paymentClient = paymentClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(@RequestParam(value = "page",defaultValue = "0") int page,
                                                 @RequestParam(value = "size",defaultValue = "1") int size,
                                                 @RequestParam(value = "sort",defaultValue = "createdAt") String sort
                                                 ) {
        return ResponseEntity.ok(
                orderService.getOrders(page,size,sort)
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(orderService.orderById(id));
    }


    @GetMapping("/username")
    public ResponseEntity<Page<Order>> getOrderByUsername(@RequestParam(value = "username") String username,
                                             @RequestParam(value = "page",defaultValue = "0") int page,
                                             @RequestParam(value = "size",defaultValue = "1") int size,
                                             @RequestParam(value = "sort",defaultValue = "createdAt") String sort) {
        return ResponseEntity.ok(
         orderService.getOrdersByUsername(username,page,size,sort)
        );
    }


    @PostMapping
    public ResponseEntity<Map<String,String>> createOrder(@RequestHeader("Authorization") String token,@RequestParam("username") String username,@RequestBody Map<String,Map<String,Double>> orders) throws JsonProcessingException {
        var list=orderService.convertToProductItems(orders);
        Order order=orderService.placeOrder(username,list);
        PaymentResponseCheckout checkoutUrl=objectMapper.readValue(paymentClient.initiatePayment(token,orderService.convertToMapParams(order)), PaymentResponseCheckout.class);
        orderService.updateOrderReference(order,checkoutUrl.getReference());
        return ResponseEntity.ok(Map.of(
                "checkout_url",checkoutUrl.getInvoiceLink()
        ));
    }

}
