package com.fortune.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.order.config.PaymentClient;
import com.fortune.order.enumeration.OrderStatus;
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


    @GetMapping("/invoice/{reference}")
    public Map<String,String> getInvoice(@RequestHeader("Authorization") String token,@PathVariable("reference") String reference) {
        return paymentClient.getInvoice(token,reference);
    }


    @PostMapping
    public ResponseEntity<Map<String,String>> createOrder(@RequestHeader("Authorization") String token,@RequestParam("username") String username,@RequestBody Map<String,Map<String,Double>> orders) throws JsonProcessingException {
        var list=orderService.convertToProductItems(orders,token);
        Order order=orderService.placeOrder(username,list);
        orderService.updateOrderStatus(order,OrderStatus.PENDING);
        PaymentResponseCheckout checkoutUrl=objectMapper.readValue(paymentClient.initiatePayment(token,orderService.convertToMapParams(order,token)), PaymentResponseCheckout.class);
        String link=checkoutUrl.getInvoiceLink();
        orderService.updateOrderReference(order,checkoutUrl.getReference());
        orderService.updatePaymentLink(order,link);
        orderService.updateOrderStatusByReference(checkoutUrl.getReference(), OrderStatus.PLACED);
        return ResponseEntity.ok(Map.of(
                "checkout_url",link
        ));
    }


    @PostMapping("/reorder/{id}")
    public ResponseEntity<Map<String,String>> redorder(@RequestHeader("Authorization") String token,@PathVariable("id") UUID id) throws JsonProcessingException {
        Order order=orderService.getOrderById(id);
        orderService.updateOrderStatus(order,OrderStatus.PENDING);
        PaymentResponseCheckout checkoutUrl=objectMapper.readValue(paymentClient.initiatePayment(token,orderService.convertToMapParams(order,token)), PaymentResponseCheckout.class);
        String link=checkoutUrl.getInvoiceLink();
        orderService.updateOrderReference(order,checkoutUrl.getReference());
        orderService.updatePaymentLink(order,link);
        orderService.updateOrderStatusByReference(checkoutUrl.getReference(), OrderStatus.PLACED);
        return ResponseEntity.ok(Map.of(
                "checkout_url",link
        ));
    }

}
