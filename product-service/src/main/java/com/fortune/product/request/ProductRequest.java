package com.fortune.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String brand;
    @NotBlank
    private String description;
    @Min(0)
    private Double price;
    @Min(0)
    private Double discount=0.0;
    @NotNull
    private List<String> images;
    @NotNull
    private String category;
    @NotNull
    private List<String> tags;
}
