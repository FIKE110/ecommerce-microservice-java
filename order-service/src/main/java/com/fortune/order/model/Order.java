package com.fortune.order.model;

import com.fortune.order.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private BigDecimal totalPrice;
    private String paymentLink;
    private LocalDateTime orderTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime shippingTime;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProductItem> products;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String txnReference;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
