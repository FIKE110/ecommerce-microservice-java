package com.fortune.cart.service;

import com.fortune.cart.config.ProductClient;
import com.fortune.cart.model.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

@Service
public class CartService {

    private final RedisTemplate<String, Cart >redisTemplate;
    private final RestClient restClient;
    private final ProductClient productClient;

    public CartService(RedisTemplate<String, Cart> redisTemplate, RestClient restClient, ProductClient productClient) {
        this.redisTemplate = redisTemplate;
        this.restClient = restClient;
        this.productClient = productClient;
    }

    public Cart getCart(Jwt jwt) {
        Cart cart=get(jwt.getSubject());
        return cart!=null?cart:new Cart();
    }

    public void addToCart(Jwt jwt,String productId) {
        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
        String username=jwt.getSubject();
        Cart cart=get(username);
        if(cart==null){
            cart=new Cart();
        }
        Integer quantity=cart.getItems().containsKey(productId) ? cart.getItems().get(productId) +1 : 1;
       cart.getItems().put(productId,quantity);
       save(username,cart);
    }

    public void removeFromCart(Jwt jwt,String productId) {
        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
        String username=jwt.getSubject();
        Cart cart= redisTemplate.opsForValue().get(username);
        assert cart != null;
        cart.getItems().remove(productId);
        save(username,cart);
    }

    public void decreaseProductInCart(Jwt jwt,String productId) {
        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
        String username=jwt.getSubject();
        Cart cart= get(username);
        assert cart != null && cart.getItems().containsKey(productId);
        if(cart.getItems().get(productId)<=0) return;
        cart.getItems().put(productId, cart.getItems().get(productId)-1);
        save(username,cart);
    }

    public void deleteCart(String username) {
        redisTemplate.delete(username+"@CART");
    }

    public void addToCartWithQuantity(Jwt jwt,String productId, int quantity) {
        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
        String username=jwt.getSubject();
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

    public boolean productExists(Jwt jwt, String productId) {
        return productClient.getProducts("Bearer "+jwt.getTokenValue(), UUID.fromString(productId)) != null;
    }

    public Cart get(String username){
        return redisTemplate.opsForValue().get(username+"@CART");
    }
}

