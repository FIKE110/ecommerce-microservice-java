package com.fortune.order.service;

import com.fortune.order.config.AuthClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthClient authClient;

    public OrderService(OrderRepository orderRepository, AuthClient authClient) {
        this.orderRepository = orderRepository;
        this.authClient = authClient;
    }

    public void updateOrderReference(Order order,String reference) {
        order.setTxnReference(reference);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    public Order placeOrder(String username, List<ProductItem> products) {
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .username(username)
                .orderTime(now)
                .status(OrderStatus.PLACED)
//                .products(products)
                .txnReference("ord_"+UUID.randomUUID().toString())
//                .totalPrice(BigDecimal.valueOf(20000))
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return orderRepository.findByUsername(username,pageable);
    }

    public void updateOrder(UUID orderId,Order orderDto) {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        order.setStatus(orderDto.getStatus());
        order.setOrderTime(orderDto.getOrderTime());
        order.setDeliveryTime(orderDto.getDeliveryTime());
//        order.setProducts(orderDto.getProducts());
        orderRepository.save(order);
    }


    public List<ProductItem> convertToProductItems(Map<String, Map<String, Double>> order) {
        List<ProductItem> productItems=new ArrayList<>();
        for(Map.Entry<String, Map<String, Double>> entry: order.entrySet()) {
            productItems.add(ProductItem.builder()
                            .productId(UUID.fromString(entry.getKey()))
                            .quantity(entry.getValue().get("quantity").longValue())
                            .price(entry.getValue().get("price"))
                    .build()
            );
        }
        return productItems;
    }

    public Map<String, String> convertToMapParams(Order order) {
        var username=order.getUsername();
        var response=authClient.profile(username).getBody();
//        var profileResponse=cust
        return Map.of(
                "amount","2000",
                "first_name","Fortune",
                "lastname","Fortune",
                "address","address",
                "item_name","Fanta",
                "quantity","1",
                "item_amount","2000",
                "email",response.get("email")
        );
    }
}
