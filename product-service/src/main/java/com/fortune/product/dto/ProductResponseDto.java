package com.fortune.product.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class ProductResponseDto {

        private UUID id;
        private Long quantity;
        private String name;
        private String brand;
        private String description;
        private Double price;
        private Double discount;
        private List<String> images;
        private String category;
        private List<String> tags;
    }
