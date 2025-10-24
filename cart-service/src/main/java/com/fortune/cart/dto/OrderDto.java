package com.fortune.cart.dto;

import com.fortune.order.enumeration.OrderStatus;
import com.fortune.order.model.ProductItem;

import java.util.List;

public class OrderDto {
    private List<ProductItem> products;
    private OrderStatus status;
}
