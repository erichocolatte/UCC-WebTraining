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
import com.example.mini_ecom.dto.cart.CartResponseDTO;
import com.example.mini_ecom.dto.cart.CartUserDTO;
import com.example.mini_ecom.model.Cart;
import com.example.mini_ecom.service.CartService;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts/user/{userId}")
    public ResponseEntity<ApiResponseDTO<?>> getAllCartsByUserId(
        @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all carts by user id successfully")
            .build())
        .data(this.cartService.handleGetAllCartsByUserId(userId))
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/carts/active/user/{userId}")
    public ResponseEntity<ApiResponseDTO<?>> getActiveCartByUserId(
        @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Get active cart by user id successfully")
                .build())
            .data(this.cartService.handleGetActiveCartByUserId(userId))
            .timeStamp(Instant.now())
            .build());
    }

    @PostMapping("/carts")
    public ResponseEntity<ApiResponseDTO<?>> createCart(
        @RequestBody Cart newCart
    ) {
        // Ao
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create cart successfully")
            .build())
        .data(this.cartService.handleCreateCart(newCart))
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getCartById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get cart successfully")
            .build())
        .data(this.cartService.handleGetCartById(id))
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/carts/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateCart(
        @PathVariable Long id,
        @RequestBody Cart updateCart
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update cart successfully")
            .build())
        .data(this.cartService.handleUpdateCart(id, updateCart))
        .timeStamp(Instant.now())
        .build());
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteCart(
        @PathVariable Long id
    ) {
        this.cartService.handleDeleteCart(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Delete cart successfully")
            .build())
        .timeStamp(Instant.now())
        .build());
    }
}
