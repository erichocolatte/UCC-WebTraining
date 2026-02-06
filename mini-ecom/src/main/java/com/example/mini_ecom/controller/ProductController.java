package com.example.mini_ecom.controller;

import java.time.Instant;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
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
import com.example.mini_ecom.dto.product.CreateProductResponseDTO;
import com.example.mini_ecom.dto.product.GetProductResponseDTO;
import com.example.mini_ecom.dto.product.ProductCategorieDTO;
import com.example.mini_ecom.dto.product.UpdateProductResponseDTO;
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
        Pageable productPageable
    ) {
        Page<Product> currentPage = this.productService.handleGetAllProducts(productPageable);
        
        List<GetProductResponseDTO> listGetProductResponseDTO = currentPage.getContent().stream().map(product -> {
            return GetProductResponseDTO.builder()
            .categorie(ProductCategorieDTO.builder()
                // .name(product.getCategorie().getName())
                .id(product.getCategorie().getId())
                .build())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .build();
        }).toList();

        PaginationResponseDTO<GetProductResponseDTO,MetaDTO> paginationResponseDTO = PaginationResponseDTO.<GetProductResponseDTO,MetaDTO>builder()
            .result(listGetProductResponseDTO)
            .meta(MetaDTO.builder()
                .page(currentPage.getNumber())
                .pageSize(currentPage.getSize())
                .pages(currentPage.getTotalPages())
                .total(currentPage.getTotalElements())
                .build()
            )
            .build();

        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all products successfully")
            .build()
        )
        .data(paginationResponseDTO)
        .timeStamp(Instant.now())
        .build());
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponseDTO<?>> createProduct(
        @Valid @RequestBody Product newProduct
    ) {
        Product createdProduct = this.productService.handleCreateProduct(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create product successfully")
            .build()
        )
        .data(CreateProductResponseDTO.builder()
            .name(createdProduct.getName())
            .description(createdProduct.getDescription())
            .price(createdProduct.getPrice())
            .stock(createdProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                // .name(createdProduct.getCategorie().getName())
                .id(createdProduct.getCategorie().getId())
                .build())
            .build())
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getProductById(
        @PathVariable Long id
    ) {
        Product currnetProduct = this.productService.handleGetProductById(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get product successfully")
            .build()
        )
        .data(GetProductResponseDTO.builder()
            .name(currnetProduct.getName())
            .description(currnetProduct.getDescription())
            .price(currnetProduct.getPrice())
            .stock(currnetProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                // .name(currnetProduct.getCategorie().getName())
                .id(currnetProduct.getCategorie().getId())
                .build())
            .build())
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateProduct(
        @PathVariable Long id,
        @RequestBody Product updateProduct
    ) {
        Product updatedProduct = this.productService.handleUpdateProduct(id, updateProduct);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update product successfully")
            .build()
        )
        .data(UpdateProductResponseDTO.builder()
            .name(updatedProduct.getName())
            .description(updatedProduct.getDescription())
            .price(updatedProduct.getPrice())
            .stock(updatedProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                // .name(updatedProduct.getCategorie().getName())
                .id(updatedProduct.getCategorie().getId())
                .build())
            .build())
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
        // Product product = this.productService.handleGetProductById(id);
        Page<Product> currentPage = this.productService.handleGetAllProductsByCategory(categoryId, productPageable);

        List<GetProductResponseDTO> listGetProductResponseDTO = currentPage.getContent().stream().map(product -> {
            return GetProductResponseDTO.builder()
            .categorie(ProductCategorieDTO.builder()
                // .name(product.getCategorie().getName())
                .id(product.getCategorie().getId())
                .build())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .build();
        }).toList();

        PaginationResponseDTO<GetProductResponseDTO,MetaDTO> paginationResponseDTO = PaginationResponseDTO.<GetProductResponseDTO,MetaDTO>builder()
            .result(listGetProductResponseDTO)
            .meta(MetaDTO.builder()
                .page(currentPage.getNumber())
                .pageSize(currentPage.getSize())
                .pages(currentPage.getTotalPages())
                .total(currentPage.getTotalElements())
                .build()
            )
            .build();

        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get product by categorie successfully")
            .build()
        )
        .data(paginationResponseDTO)
        .timeStamp(Instant.now())
        .build());
    }
}
