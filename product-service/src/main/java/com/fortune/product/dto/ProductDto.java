package com.fortune.product.dto;

import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID id;

    private String name;
    private String brand;
    private String description;
    private Double price;
    private Double discount;
    private List<String> images;
    private String category;
    private List<String> tags;
}
