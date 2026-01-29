package com.example.mini_ecom.controller;

import java.time.Instant;
import java.util.List;

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
import com.example.mini_ecom.dto.product_assest.CreateProductAssestResponseDTO;
import com.example.mini_ecom.dto.product_assest.GetProductAssestResponseDTO;
import com.example.mini_ecom.dto.product_assest.ProductAssestProduct;
import com.example.mini_ecom.dto.product_assest.UpdateProductAssestResponseDTO;
import com.example.mini_ecom.model.ProductAssest;
import com.example.mini_ecom.service.ProductAssestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProductAssestController {
    private final ProductAssestService productAssestService;

    public ProductAssestController(ProductAssestService productAssestService) {
        this.productAssestService = productAssestService;
    }

    @GetMapping("/product-assests/product/{productid}")
    public ResponseEntity<ApiResponseDTO<?>> getAllProductAssests(
        @PathVariable("productid") Long productId
    ) {
        List<GetProductAssestResponseDTO> listProductAssests = this.productAssestService.handleGetProductAssestByProduct(productId).stream().map(product_assest -> {
            return GetProductAssestResponseDTO.builder()
                .assest_url(product_assest.getAssest_url())
                .is_main(product_assest.getIs_main())
                .product(ProductAssestProduct.builder()
                    .id(product_assest.getProduct().getId())
                    .build())
                .build();
        }).toList();
        
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all product assests successfully")
            .build()
        )
        .data(listProductAssests)
        .timeStamp(Instant.now())
        .build());
    }

    @PostMapping("/product-assests")
    public ResponseEntity<ApiResponseDTO<?>> createProductAssest(
        @Valid @RequestBody ProductAssest newProductAssest
    ) {
        ProductAssest createdProductAssest = this.productAssestService.handleCreateProductAssest(newProductAssest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Create product assest successfully")
                .build()
            )
            .data(CreateProductAssestResponseDTO.builder()
                .assest_url(createdProductAssest.getAssest_url())
                .is_main(createdProductAssest.getIs_main())
                .product(ProductAssestProduct.builder()
                    .id(createdProductAssest.getProduct().getId())
                    .build())
                .build())
            .timeStamp(Instant.now())
            .build());
    }

    @PutMapping("/product-assests/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateProductAssest(
        @PathVariable("id") Long id,
        @RequestBody ProductAssest updateProductAssest
    ) {
        ProductAssest updatedProductAssest = this.productAssestService.handleUpdateProductAssest(id, updateProductAssest);
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Update product assest successfully")
                .build()
            )
            .data(UpdateProductAssestResponseDTO.builder()
                .assest_url(updatedProductAssest.getAssest_url())
                .is_main(updatedProductAssest.getIs_main())
                .product(ProductAssestProduct.builder()
                    .id(updatedProductAssest.getProduct().getId())
                    .build())
                .build())
            .timeStamp(Instant.now())
            .build());
    }

    @DeleteMapping("/product-assests/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteProductAssest(
        @PathVariable("id") Long id
    ) {
        this.productAssestService.handleDeleteProductAssest(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Delete product assest successfully")
                .build()
            )
            .timeStamp(Instant.now())
            .build());
    }
}
