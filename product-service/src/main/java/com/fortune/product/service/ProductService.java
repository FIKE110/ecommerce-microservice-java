package com.fortune.product.service;

import com.fortune.Event;
import com.fortune.EventType;
import com.fortune.product.client.InventoryClient;
import com.fortune.product.dto.ProductDto;
import com.fortune.product.dto.ProductResponseDto;
import com.fortune.product.entity.Product;
import com.fortune.product.repository.ProductRepository;
import com.fortune.product.request.ProductRequest;
import com.fortune.product.spec.ProductSpecification;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitMQService rabbitMQService;
    private final ModelMapper modelMapper;
    private final InventoryClient inventoryClient;

    public ProductService(ProductRepository productRepository, RabbitMQService rabbitMQService, ModelMapper modelMapper, InventoryClient inventoryClient) {
        this.productRepository = productRepository;
        this.rabbitMQService = rabbitMQService;
        this.modelMapper = modelMapper;
        this.inventoryClient = inventoryClient;
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
                .discount(request.getDiscount())
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

    public ProductResponseDto getProductById(UUID id) {
        var product=productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));
        var dto=modelMapper.map(product,ProductResponseDto.class);
        dto.setQuantity(Long.parseLong(inventoryClient.getProductInventory(id).getBody().get("quantity")));
        return dto;
    }

    public Double getProductPriceById(UUID id) {
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found")).getPrice();
    }

    public Page<Product> getProducts(
            Integer page,
            Integer size,
            String sort,
            String name,
            String category,
            Double minPrice,
            Double maxPrice
    ) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(sort != null ? sort : "createdAt").descending()
        );

        Specification<Product> spec = Specification
                .where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.minPrice(minPrice))
                .and(ProductSpecification.maxPrice(maxPrice));

        return productRepository.findAll(spec, pageable);
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
