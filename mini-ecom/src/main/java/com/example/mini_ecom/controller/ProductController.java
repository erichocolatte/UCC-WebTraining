package com.example.mini_ecom.controller;

import java.time.Instant;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mini_ecom.dto.ApiResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.dto.product.ProductAssestDTO;
import com.example.mini_ecom.dto.product.ProductCategorieDTO;
import com.example.mini_ecom.dto.product.ProductResponseDTO;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponseDTO<?>> getAllProducts(
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable productPageable
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all products successfully")
            .build()
        )
        .data(this.productService.handleGetAllProducts(productPageable))
        .timeStamp(Instant.now())
        .build());
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponseDTO<?>> createProduct(
        @Valid @RequestBody Product newProduct
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create product successfully")
            .build()
        )
        .data(this.productService.handleCreateProduct(newProduct))
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getProductById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get product successfully")
            .build()
        )
        .data(this.productService.handleGetProductById(id))
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateProduct(
        @PathVariable Long id,
        @RequestBody Product updateProduct
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update product successfully")
            .build()
        )
        .data(this.productService.handleUpdateProduct(id, updateProduct))
        .timeStamp(Instant.now())
        .build());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteProduct(
        @PathVariable Long id
    ) {
        this.productService.handleDeleteProduct(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Delete product successfully")
            .build()
        )
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/products/categorie/{categoryId}")
    public ResponseEntity<ApiResponseDTO<?>> getAllProductsByCategory(
        @PathVariable Long categoryId,
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable productPageable
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get product by categorie successfully")
            .build()
        )
        .data(this.productService.handleGetAllProductsByCategory(categoryId, productPageable))
        .timeStamp(Instant.now())
        .build());
    }
}
