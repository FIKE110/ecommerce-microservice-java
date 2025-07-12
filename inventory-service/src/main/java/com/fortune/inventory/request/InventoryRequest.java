package com.fortune.inventory.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;


public record InventoryRequest (
        @NonNull
        UUID productId,
        @NotNull
        Long quantity
){}
