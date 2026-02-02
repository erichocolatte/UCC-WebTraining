package com.example.mini_ecom.controller;

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
import com.example.mini_ecom.dto.cart_item.CartItemCartDTO;
import com.example.mini_ecom.dto.cart_item.CartItemProductDTO;
import com.example.mini_ecom.dto.cart_item.CartItemResponseDTO;
import com.example.mini_ecom.model.CartItem;
import com.example.mini_ecom.service.CartItemService;

@RestController
@RequestMapping("/api/v1")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/cart-items/cart/{cartId}")
    public ResponseEntity<ApiResponseDTO<?>> getCartItemsByCartId(
        @PathVariable Long cartId
    ) {
        List<CartItem> listCartItem = this.cartItemService.handleGetCartItemsByCartId(cartId);
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Get cart items by cart id sucessfully")
                .build()
            )
            .data(listCartItem.stream().map(cartItem -> CartItemResponseDTO.builder()
                .quantity(cartItem.getQuantity())
                .price_at_time(cartItem.getPrice_at_time())
                .cart(CartItemCartDTO.builder()
                    .id(cartItem.getCart().getId())
                    .build())
                .product(CartItemProductDTO.builder()
                    .id(cartItem.getProduct().getId())
                    .build())
                .build()).toList())
            .build()
        );
    }

    @PostMapping("/cart-items")
    private ResponseEntity<ApiResponseDTO<?>> createCartItem(
        @RequestBody CartItem newCartItem
    ) {
       CartItem createdCartItem = this.cartItemService.handleCreateCartItem(newCartItem);
       
       return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Create cart item sucessfully")
                .build()
            )
            .data(CartItemResponseDTO.builder()
                .quantity(createdCartItem.getQuantity())
                .price_at_time(createdCartItem.getPrice_at_time())
                .cart(CartItemCartDTO.builder()
                    .id(createdCartItem.getCart().getId())
                    .build())
                .product(CartItemProductDTO.builder()
                    .id(createdCartItem.getProduct().getId())
                    .build())
                .build())
            .build()
        );
    }

    @PutMapping("/cart-items/{id}")
    private ResponseEntity<ApiResponseDTO<?>> updateCartItem(
        @PathVariable Long id,
        @RequestBody CartItem updateCartItem
    ) {
        CartItem updatedCartItem = this.cartItemService.handleUpdateCartItem(id, updateCartItem);
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Update cart item sucessfully")
                .build()
            )
            .data(CartItemResponseDTO.builder()
                .quantity(updatedCartItem.getQuantity())
                .price_at_time(updatedCartItem.getPrice_at_time())
                .cart(CartItemCartDTO.builder()
                    .id(updatedCartItem.getCart().getId())
                    .build())
                .product(CartItemProductDTO.builder()
                    .id(updatedCartItem.getProduct().getId())
                    .build())
                .build())
            .build()
        );
    }

    @DeleteMapping("/cart-items/{id}")
    private ResponseEntity<ApiResponseDTO<?>> deleteCartItem(
        @PathVariable Long id
    ) {
        this.cartItemService.handleDeleteCartItem(id);
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Delete cart item sucessfully")
                .build())
            .build()
        );
    }
}
