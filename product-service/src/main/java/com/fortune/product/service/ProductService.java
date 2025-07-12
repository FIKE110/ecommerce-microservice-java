package com.fortune.product.service;

import com.fortune.Event;
import com.fortune.EventType;
import com.fortune.product.dto.ProductDto;
import com.fortune.product.entity.Product;
import com.fortune.product.repository.ProductRepository;
import com.fortune.product.request.ProductRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitMQService rabbitMQService;

    public ProductService(ProductRepository productRepository, RabbitMQService rabbitMQService) {
        this.productRepository = productRepository;
        this.rabbitMQService = rabbitMQService;
    }

    @Transactional
    public void createProduct(String seller,ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .seller(seller)
                .price(request.getPrice())
                .brand(request.getBrand())
                .category(request.getCategory())
                .tags(request.getTags())
                .rating(5.0)
                .description(request.getDescription())
                .images(request.getImages())
                .discount(0.0)
                .build();
        product=productRepository.save(product);
        rabbitMQService.sendMessage(
                Event.builder()
                        .id(UUID.randomUUID().toString())
                        .eventType(EventType.PRODUCT_CREATED)
                        .message(Map.of("id",product.getId().toString(),"quantity",request.getQuantity().toString()))
                        .build()
        );
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found"));
    }

    public Page<Product> getProducts(Integer page, Integer size, String sort) {
        var request=PageRequest.of(0, 10, Sort.by(sort));
        return productRepository.findAll(request);
    }

    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }

    @Transactional
    @Modifying
    public void updateProduct(UUID id, ProductRequest request) {
        Product product=productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found"));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setDiscount(request.getDiscount());
       product.setCategory(request.getCategory());
       product.setTags(request.getTags());
       product.setDescription(request.getDescription());
       product.setImages(request.getImages());
       productRepository.save(product);
    }



}
