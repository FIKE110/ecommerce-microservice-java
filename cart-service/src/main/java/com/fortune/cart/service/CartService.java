package com.fortune.cart.service;

import com.fortune.Event;
import com.fortune.EventType;
import com.fortune.cart.config.OrderClient;
import com.fortune.cart.config.ProductClient;
import com.fortune.cart.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CartService {

    private final OrderClient orderClient;
    private final RedisTemplate<String, Cart >redisTemplate;
    private final RestClient restClient;
    private final ProductClient productClient;
    private final RabbitMQService rabbitMQService;

    public CartService(RedisTemplate<String, Cart> redisTemplate, RestClient restClient, ProductClient productClient, RabbitMQService rabbitMQService, OrderClient orderClient) {
        this.redisTemplate = redisTemplate;
        this.restClient = restClient;
        this.productClient = productClient;
        this.rabbitMQService = rabbitMQService;
        this.orderClient=orderClient;
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
        Double quantity=cart.getItems().containsKey(productId) ? cart.getItems().get(productId).get("quantity") +1 : 1;
        Double price=productPrice(jwt,productId);
        if(price==null) throw new RuntimeException("product price does not exist");
        cart.getItems().put(productId,Map.of(
               "quantity",quantity,
               "price", price
       ));
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

    public String checkout(Jwt jwt) {
        String username=jwt.getSubject();
        Cart cart=get(username);
        if(cart==null) throw new RuntimeException("No cart to checkout");
        if(cart.getItems().isEmpty()) throw new RuntimeException("No cart to checkout");
        Map<String,String> url= orderClient.createOrder("Bearer "+jwt.getTokenValue(),username,cart.getItems()).getBody();
        getAndDelete(username);
        assert url != null;
        assert !url.isEmpty();
        assert url.get("checkout_url")!=null;
        return url.get("checkout_url");
    }

    public void decreaseProductInCart(Jwt jwt,String productId) {
        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
        String username=jwt.getSubject();
        Cart cart= get(username);
        assert cart != null && cart.getItems().containsKey(productId);
        if(cart.getItems().get(productId).get("quantity")<=0) return;
        cart.getItems().put(productId,
                Map.of(
                        "quantity",cart.getItems().get(productId).get("quantity")-1,
                        "price",cart.getItems().get(productId).get("price")
                        ));
        save(username,cart);
    }

    public void deleteCart(String username) {
        redisTemplate.delete(username+"@CART");
    }

//    public void addToCartWithQuantity(Jwt jwt,String productId, int quantity) {
//        if(!productExists(jwt, productId)) throw new RuntimeException("product does not exist");
//        String username=jwt.getSubject();
//        Cart cart=get(username+"@CART");
//        if(cart==null){
//            cart=new Cart();
//        }
//        cart.getItems().put(productId,Map.of("quantity",cart.getItems().containsKey(productId) ?cart.getItems().get(productId).get("quantity")+quantity: quantity),
//        "price",cart.getItems().get(productId).get("price")
//        );
//        save(username,cart);
//    }

    public void save(String username,Cart cart){
        redisTemplate.opsForValue().set(username+"@CART",cart, Duration.ofHours(12));
    }

    public boolean productExists(Jwt jwt, String productId) {
        return productClient.getProduct("Bearer "+jwt.getTokenValue(), UUID.fromString(productId)) != null;
    }

    public Double productPrice(Jwt jwt, String productId) {
       return productClient.getProductPrice("Bearer "+jwt.getTokenValue(), UUID.fromString(productId)).get("price");
    }

    public Cart get(String username){
        return redisTemplate.opsForValue().get(username+"@CART");
    }

    public Cart getAndDelete(String username){
        return redisTemplate.opsForValue().getAndDelete(username+"@CART");
    }
}

