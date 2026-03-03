package com.fortune.cart.dto;

import com.fortune.cart.enumeration.OrderStatus;
import com.fortune.cart.model.ProductItem;

import java.util.List;

public class OrderDto {
    private List<ProductItem> products;
    private OrderStatus status;
}
