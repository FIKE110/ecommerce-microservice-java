package com.fortune.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.order.config.AuthClient;
import com.fortune.order.config.CustomerClient;
import com.fortune.order.config.PaymentClient;
import com.fortune.order.config.ProductClient;
import com.fortune.order.enumeration.OrderStatus;
import com.fortune.order.model.Order;
import com.fortune.order.model.ProductItem;
import com.fortune.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthClient authClient;
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, AuthClient authClient, PaymentClient paymentClient, CustomerClient customerClient, ProductClient productClient, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.authClient = authClient;
        this.paymentClient = paymentClient;
        this.objectMapper=objectMapper;
        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("<UNK>"));
    }

    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void updateOrderReference(Order order,String reference) {
        order.setTxnReference(reference);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    public void updatePaymentLink(Order order, String paymentLink) {
        order.setPaymentLink(paymentLink);
        orderRepository.save(order);
    }

    public Optional<Order> findByReference(String reference) {
        return orderRepository.findByTxnReference(reference);
    }

    public Order placeOrder(String username, List<ProductItem> products) {
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .username(username)
                .orderTime(now)
                .status(OrderStatus.PENDING)
                .products(products)
                .txnReference(null)
                .totalPrice(BigDecimal.valueOf(products.stream().mapToDouble(ProductItem::getPrice).sum()))
                .build();
       return orderRepository.save(order);
    }

    public void updateOrderStatusByReference(String reference,OrderStatus status) {
       orderRepository.findByTxnReference(reference).ifPresent(
               order->{order.setStatus(status);
                                orderRepository.save(order);}
       );

    }

    public void updateOrderShipingAndDelivery(Order order) {
        Random random=new Random();
        order.setDeliveryTime(LocalDateTime.now().plusMinutes(10+random.nextInt(5)));
        order.setShippingTime(LocalDateTime.now().plusMinutes(random.nextInt(5)));
        orderRepository.save(order);
    }

    public void cancelOrder(UUID orderId) {
       Order order= orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
    }

    public Order orderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Page<Order> getOrders(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(sort));
        return orderRepository.findAll(pageable);
    }

    public Page<Order> getOrdersByUsername(String username, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return orderRepository.findByUsername(username,pageable);
    }


    public void updateOrder(UUID orderId,Order orderDto) {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        order.setStatus(orderDto.getStatus());
        order.setOrderTime(orderDto.getOrderTime());
        order.setDeliveryTime(orderDto.getDeliveryTime());
        orderRepository.save(order);
    }


    public List<ProductItem> convertToProductItems(Map<String, Map<String, Double>> order,String token) {
        List<ProductItem> productItems=new ArrayList<>();
        for(Map.Entry<String, Map<String, Double>> entry: order.entrySet()) {
            productItems.add(ProductItem.builder()
                            .productId(UUID.fromString(entry.getKey()))
                            .quantity(entry.getValue().get("quantity").longValue())
                            .productName(productClient.getProductName(token, entry.getKey()).getBody().get("name"))
                            .price(entry.getValue().get("price"))
                    .build()
            );
        }
        return productItems;
    }

    public Map<String, String> convertToMapParams(Order order,String token) throws JsonProcessingException {
        var username=order.getUsername();
        var response=authClient.profile(username).getBody();
        var res=customerClient.getCustomerProfile(token);
        double total = order.getProducts().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();


        return Map.of(
                "amount",Double.toString(total),
                "firstName",res.getBody().get("firstName"),
                "lastName",res.getBody().get("lastName"),
                "address",res.getBody().get("address"),
                "email",response.get("email"),
                "items",objectMapper.writeValueAsString(order.getProducts())
        );
    }
}
