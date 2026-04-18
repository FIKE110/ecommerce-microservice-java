package com.fortune.product.controller;

import com.fortune.utils.ApiDataResponse;
import com.fortune.utils.ApiResponse;
import com.fortune.utils.DataWrapper;
import com.fortune.utils.MessageInString;
import com.fortune.product.dto.ProductResponseDto;
import com.fortune.product.entity.Product;
import com.fortune.product.enumeration.ProductResponseCode;
import com.fortune.product.request.ProductRequest;
import com.fortune.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = {"${api.url}","${api.url}/"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, MessageInString>>> createProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid  ProductRequest request) {
        productService.createProduct(jwt.getSubject(),request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
               ApiResponse.data(
                                ProductResponseCode.PRODUCT_CREATED,
                                new MessageInString("Product created successfully")
                        )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, ProductResponseDto>>> getProductById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
              ApiResponse.data(
                                ProductResponseCode.PRODUCT_FETCHED,
                                productService.getProductById(id)
                        )

        );
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Void> getProductImageById(@PathVariable UUID id) {

        var product = productService.getProductById(id);

        if (product.getImages() == null || product.getImages().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)   // 302 redirect
                .location(URI.create(product.getImages().get(0)))
                .build();
    }


    @GetMapping("/{id}/name")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String,String>> getProductNameById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                Map.of(
                    "name",    productService.getProductById(id).getName()
                )

        );
    }

    @GetMapping("/{id}/price")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String,Double>> getProductPriceById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                        Map.of("price",productService.getProductPriceById(id))
        );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, Page<Product>>>> getProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {

        return ResponseEntity.ok(
                ApiResponse.data(
                        ProductResponseCode.PRODUCTS_FETCHED,
                        productService.getProducts(
                                page,
                                size,
                                sort,
                                name,
                                category,
                                minPrice,
                                maxPrice
                        )
                )
        );
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, MessageInString>>> updateProduct(@PathVariable("id") UUID id, @RequestBody @Valid ProductRequest request) {
        productService.updateProduct(id, request);
        return ResponseEntity.ok(
                ApiResponse.data(
                                ProductResponseCode.PRODUCT_UPDATED,
                                new MessageInString("Product is updated")
                        )

                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, MessageInString>>> createProduct(@PathVariable("id") UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok(
               ApiResponse.data(
                                ProductResponseCode.PRODUCT_DELETED,
                             new  MessageInString("Product deleted")
                        )

                );
    }

}
