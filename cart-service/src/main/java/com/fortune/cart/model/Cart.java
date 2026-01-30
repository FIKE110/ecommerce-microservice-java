package com.fortune.cart.model;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    private UUID id=UUID.randomUUID();
    private HashMap<String,Map<String,Double>> items=new HashMap<>();
}
