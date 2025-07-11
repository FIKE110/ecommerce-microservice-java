package com.fortune.cart.service;

import com.fortune.cart.model.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CartService {

    private final RedisTemplate<String, Cart >redisTemplate;

    public CartService(RedisTemplate<String, Cart> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToCart(String username,String productId) {
        Cart cart=get(username);
        if(cart==null){
            cart=new Cart();
        }
        Integer quantity=cart.getItems().containsKey(productId) ? cart.getItems().get(productId) +1 : 1;
       cart.getItems().put(productId,quantity);
       save(username,cart);
    }

    public void removeFromCart(String username,String productId) {
        Cart cart= redisTemplate.opsForValue().get(username);
        assert cart != null;
        cart.getItems().remove(productId);
        save(username,cart);
    }

    public void decreaseProductInCart(String username,String productId) {
        Cart cart= get(username);
        assert cart != null && cart.getItems().containsKey(productId);
        if(cart.getItems().get(productId)<=0) return;
        cart.getItems().put(productId, cart.getItems().get(productId)-1);
        save(username,cart);
    }

    public void deleteCart(String username) {
        redisTemplate.delete(username+"@CART");
    }

    public void addToCartWithQuantity(String username,String productId, int quantity) {
        Cart cart=get(username+"@CART");
        if(cart==null){
            cart=new Cart();
        }
        cart.getItems().put(productId, cart.getItems().containsKey(productId) ?cart.getItems().get(productId)+quantity: quantity);
        save(username,cart);
    }

    public void save(String username,Cart cart){
        redisTemplate.opsForValue().set(username+"@CART",cart, Duration.ofHours(12));
    }

    public Cart get(String username){
        return redisTemplate.opsForValue().get(username+"@CART");
    }
}

