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
        List<CartResponseDTO> listCartResponseDTO = this.cartService.handleGetAllCartsByUserId(userId).stream().map(cart -> CartResponseDTO.builder()
            .id(cart.getId())
            .status(cart.getStatus())
            .user(CartUserDTO.builder()
                .id(cart.getUser().getId())
                .build())
            .build()).toList();
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all carts by user id successfully")
            .build())
        .data(listCartResponseDTO)
        .timeStamp(Instant.now())
        .build());
    }

    @PostMapping("/carts")
    public ResponseEntity<ApiResponseDTO<?>> createCart(
        @RequestBody Cart newCart
    ) {
        Cart createdCart = this.cartService.handleCreateCart(newCart);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create cart successfully")
            .build())
        .data(CartResponseDTO.builder()
            .id(createdCart.getId())
            .status(createdCart.getStatus())
            .user(CartUserDTO.builder()
                .id(createdCart.getUser().getId())
                .build())
            .build())
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getCartById(
        @PathVariable Long id
    ) {
        Cart currentCart = this.cartService.handleGetCartById(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get cart successfully")
            .build())
        .data(CartResponseDTO.builder()
            .id(currentCart.getId())
            .status(currentCart.getStatus())
            .user(CartUserDTO.builder()
                .id(currentCart.getUser().getId())
                .build())
            .build())
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/carts/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateCart(
        @PathVariable Long id,
        @RequestBody Cart updateCart
    ) {
        Cart updatedCart = this.cartService.handleUpdateCart(id, updateCart);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update cart successfully")
            .build())
        .data(CartResponseDTO.builder()
            .id(updatedCart.getId())
            .status(updatedCart.getStatus())
            .user(CartUserDTO.builder()
                .id(updatedCart.getUser().getId())
                .build())
            .build())
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
