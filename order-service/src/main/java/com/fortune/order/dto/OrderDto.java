package com.fortune.order.dto;

import com.fortune.order.enumeration.OrderStatus;
import com.fortune.order.model.ProductItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {
    private List<ProductItem> products;
    private OrderStatus status;
}
