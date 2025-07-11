package com.fortune.product.controller;

import com.fortune.ApiDataResponse;
import com.fortune.DataWrapper;
import com.fortune.MessageInString;
import com.fortune.product.dto.ProductDto;
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
                new ApiDataResponse<>(
                        new DataWrapper<>(
                                ProductResponseCode.PRODUCT_CREATED,
                                new MessageInString("Product created successfully")
                        )
                )
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, Product>>> getProductById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        new DataWrapper<>(
                                ProductResponseCode.PRODUCT_FETCHED,
                                productService.getProductById(id)
                        )

        ));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, Page<Product>>>> getProducts(
            @RequestParam(value = "sort",defaultValue = "createdAt") String sort,@RequestParam(value = "page",defaultValue = "0") Integer page,
            @RequestParam(value = "size",defaultValue = "1") Integer size
    ) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        new DataWrapper<>(
                                ProductResponseCode.PRODUCTS_FETCHED,
                                productService.getProducts(page, size, sort)
                        )

                ));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, MessageInString>>> updateProduct(@PathVariable("id") UUID id, @RequestBody @Valid ProductRequest request) {
        productService.updateProduct(id, request);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        new DataWrapper<>(
                                ProductResponseCode.PRODUCT_UPDATED,
                                new MessageInString("Product is updated")
                        )

                ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiDataResponse<DataWrapper<ProductResponseCode, MessageInString>>> createProduct(@PathVariable("id") UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        new DataWrapper<>(
                                ProductResponseCode.PRODUCT_DELETED,
                             new  MessageInString("Product deleted")
                        )

                ));
    }

}
