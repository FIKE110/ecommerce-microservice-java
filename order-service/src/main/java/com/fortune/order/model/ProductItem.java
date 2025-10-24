package com.fortune.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


//@Entity
@Data
@Builder
public class ProductItem{

    private UUID productId;
    private Long quantity;
    private Double price;
}
