package com.fortune.product.service;

import com.fortune.product.entity.Product;
import com.fortune.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializerService {

    private final ProductRepository productRepository;

    public DataInitializerService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() > 0) return; // Prevent duplicate inserts

        List<Product> products = List.of(
                Product.builder()
                        .name("Wireless Bluetooth Headphones")
                        .brand("Sony")
                        .description("High-quality over-ear Bluetooth headphones with noise cancellation and 30-hour battery life.")
                        .price(85000.00)
                        .discount(10.0)
                        .seller("Jumia Electronics")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1518449037369-7b3d1aebc7a6",
                                "https://images.unsplash.com/photo-1517423440428-a5a00ad493e8",
                                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
                                "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f"
                        ))
                        .category("Electronics")
                        .tags(List.of("audio", "bluetooth", "headphones", "sony"))
                        .rating(4.6)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Product.builder()
                       
                        .name("Smart LED TV 55-Inch")
                        .brand("Samsung")
                        .description("Ultra HD Smart TV with HDR support, crystal-clear display, and built-in streaming apps.")
                        .price(310000.00)
                        .discount(15.0)
                        .seller("Slot Nigeria")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04",
                                "https://images.unsplash.com/photo-1606813909025-442fcd14a421",
                                "https://images.unsplash.com/photo-1550684376-efcbd6e3f031",
                                "https://images.unsplash.com/photo-1587825140708-51c7b8f5ec2e"
                        ))
                        .category("Home Appliances")
                        .tags(List.of("tv", "smart", "uhd", "electronics"))
                        .rating(4.8)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Product.builder()
                       
                        .name("Men’s Running Sneakers")
                        .brand("Nike")
                        .description("Lightweight, durable sneakers designed for comfort and performance.")
                        .price(55000.00)
                        .discount(5.0)
                        .seller("Mega Sports Shop")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1528701800489-20be9c6e30b7",
                                "https://images.unsplash.com/photo-1508182311256-e3f7d23a2400",
                                "https://images.unsplash.com/photo-1579338559194-a162d19bf842",
                                "https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb"
                        ))
                        .category("Fashion")
                        .tags(List.of("shoes", "men", "sports", "nike"))
                        .rating(4.4)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Product.builder()
                       
                        .name("Women’s Designer Handbag")
                        .brand("Michael Kors")
                        .description("Elegant leather handbag with gold-tone hardware and spacious compartments.")
                        .price(120000.00)
                        .discount(20.0)
                        .seller("Luxury Avenue")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1618354691235-74df6e8af3b3",
                                "https://images.unsplash.com/photo-1606813909440-9a0f6e5c8e3d",
                                "https://images.unsplash.com/photo-1603898037225-4e4d3db0c0b5",
                                "https://images.unsplash.com/photo-1618354691184-6c9dc6c2b3e9"
                        ))
                        .category("Fashion")
                        .tags(List.of("bag", "women", "luxury", "accessory"))
                        .rating(4.7)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                Product.builder()
                       
                        .name("Gaming Laptop 16GB RAM")
                        .brand("Asus ROG")
                        .description("High-performance gaming laptop with RTX 3060 GPU and fast refresh rate display.")
                        .price(870000.00)
                        .discount(10.0)
                        .seller("Tech Haven")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8",
                                "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9",
                                "https://images.unsplash.com/photo-1612832021151-4caa4a9924b9",
                                "https://images.unsplash.com/photo-1625772452859-1b134f715a65"
                        ))
                        .category("Computers")
                        .tags(List.of("laptop", "gaming", "rog", "asus"))
                        .rating(4.9)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                // 6. Beauty & Personal Care
                Product.builder()
                       
                        .name("Shea Butter Moisturizing Cream")
                        .brand("Natural Glow")
                        .description("Organic shea butter cream enriched with coconut oil and aloe vera for smooth skin.")
                        .price(4500.00)
                        .discount(0.0)
                        .seller("Beauty Mart")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1588159343745-4452c31f3a17",
                                "https://images.unsplash.com/photo-1627222298301-94b0eec0f8f1",
                                "https://images.unsplash.com/photo-1556228578-0e0b7d7d6b22",
                                "https://images.unsplash.com/photo-1588692044659-2d82c9b9b11f"
                        ))
                        .category("Beauty")
                        .tags(List.of("cream", "shea", "moisturizer", "natural"))
                        .rating(4.3)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                // 7. Grocery
                Product.builder()
                       
                        .name("Royal Stallion Rice 50kg")
                        .brand("Royal Stallion")
                        .description("Premium parboiled long-grain rice perfect for jollof, fried rice, and white rice.")
                        .price(68000.00)
                        .discount(5.0)
                        .seller("Shoprite Nigeria")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f",
                                "https://images.unsplash.com/photo-1627308595241-7e6b2e1b832a",
                                "https://images.unsplash.com/photo-1590080875839-788c6d0b4bb1",
                                "https://images.unsplash.com/photo-1627308595262-4adcd1b06d6c"
                        ))
                        .category("Groceries")
                        .tags(List.of("rice", "food", "grain", "staple"))
                        .rating(4.5)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                // 8. Phone
                Product.builder()
                       
                        .name("iPhone 14 Pro 256GB")
                        .brand("Apple")
                        .description("Latest iPhone with A16 Bionic chip, dynamic island, and triple camera system.")
                        .price(1250000.00)
                        .discount(10.0)
                        .seller("Apple Store Lagos")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1661961112958-24c65e48352e",
                                "https://images.unsplash.com/photo-1661961112959-91d3c8bb8d2d",
                                "https://images.unsplash.com/photo-1661961112960-bb1d8a772a63",
                                "https://images.unsplash.com/photo-1661961112961-7a5646d489f9"
                        ))
                        .category("Phones")
                        .tags(List.of("iphone", "apple", "smartphone"))
                        .rating(4.9)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                // 9. Watch
                Product.builder()
                       
                        .name("Apple Watch Series 9")
                        .brand("Apple")
                        .description("Smartwatch with health tracking, fitness monitoring, and GPS support.")
                        .price(520000.00)
                        .discount(5.0)
                        .seller("Gadget World")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1552508744-1696d4464960",
                                "https://images.unsplash.com/photo-1603791452906-c7b1c0dcb8a0",
                                "https://images.unsplash.com/photo-1552508744-7d5f1b3c6a19",
                                "https://images.unsplash.com/photo-1586810169502-1d1d5f7bba83"
                        ))
                        .category("Accessories")
                        .tags(List.of("smartwatch", "apple", "fitness"))
                        .rating(4.7)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),

                // 10. Perfume
                Product.builder()
                       
                        .name("Bleu de Chanel Perfume")
                        .brand("Chanel")
                        .description("Luxury men’s perfume with a woody, aromatic scent for all occasions.")
                        .price(155000.00)
                        .discount(10.0)
                        .seller("Essenza Nigeria")
                        .images(List.of(
                                "https://images.unsplash.com/photo-1616627459601-5c5dc53f3e5a",
                                "https://images.unsplash.com/photo-1622560480615-cd4eab7c8e8a",
                                "https://images.unsplash.com/photo-1585386959984-a4155225f1b3",
                                "https://images.unsplash.com/photo-1616627459705-06b67ff2f57a"
                        ))
                        .category("Fragrances")
                        .tags(List.of("perfume", "men", "chanel"))
                        .rating(4.9)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        productRepository.saveAll(products);
        System.out.println("✅ Successfully seeded 20 products into the database!");
    }
}
