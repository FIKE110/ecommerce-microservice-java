package com.fortune.cart.controller;

import com.fortune.cart.model.Cart;
import com.fortune.cart.service.CartService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = {"${api.url}","${api.url}/"})
public class CartController {

    private final CartService cartService;

    public CartController( CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Integer>> getCart(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartService.getCart(jwt).getItems());
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<?> saveInCache(@AuthenticationPrincipal Jwt jwt, @PathVariable("productId") UUID productId) {
        cartService.addToCart(jwt,productId.toString());
       return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/remove")
    public ResponseEntity<?> removeFromCache(@AuthenticationPrincipal Jwt jwt,@PathVariable("productId") UUID productId) {
        cartService.decreaseProductInCart(jwt,productId.toString());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/delete")
    public ResponseEntity<?> deleteFromCache(@AuthenticationPrincipal Jwt jwt,@PathVariable("productId") UUID productId) {
        cartService.removeFromCart(jwt,productId.toString());
        return ResponseEntity.noContent().build();
    }
}
