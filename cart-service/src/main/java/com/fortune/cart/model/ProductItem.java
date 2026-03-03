package com.fortune.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {
    private String productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}
