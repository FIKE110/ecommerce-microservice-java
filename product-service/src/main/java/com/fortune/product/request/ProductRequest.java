package com.fortune.product.request;

import jakarta.validation.constraints.*;
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
    @DecimalMin(value = "0")
    @NotNull
    private Double price;
    @Min(0)
    private Double discount=0.0;
    @DecimalMin("0")
    @NotNull
    private Long quantity;
    @NotNull
    private List<String> images;
    @NotNull
    private String category;
    @NotNull
    private List<String> tags;
}
