package com.fortune.order.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


//@Entity
@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem{
    private UUID productId;
    private String productName;
    private Long quantity;
    private Double price;
}
