package com.fortune.cart.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Builder.Default
    private HashMap<String, Map<String, Double>> items = new HashMap<>();

    public Map<String, Map<String, Double>> getItems() {
        return items;
    }
}
