package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.cart_item.CartItemCartDTO;
import com.example.mini_ecom.dto.cart_item.CartItemProductDTO;
import com.example.mini_ecom.dto.cart_item.CartItemResponseDTO;
import com.example.mini_ecom.model.Cart;
import com.example.mini_ecom.model.CartItem;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.repository.CartItemRepository;
import com.example.mini_ecom.repository.CartRepository;
import com.example.mini_ecom.repository.ProductRepository;
import com.example.mini_ecom.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository,
            ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CartItemResponseDTO handleCreateCartItem(CartItem newCartItem) {
        if (newCartItem.getCart() == null) {
            throw new IllegalArgumentException("Cart is required");
        } else {
            this.cartRepository.findByIdAndDeletedAtIsNull(newCartItem.getCart().getId()).orElseThrow(() -> 
            new NoSuchElementException("Cart not found"));
        }

        if (newCartItem.getProduct() == null) {
            throw new IllegalArgumentException("Product is required");
        } else {
            this.productRepository.findByIdAndDeletedAtIsNull(newCartItem.getProduct().getId()).orElseThrow(() -> 
            new NoSuchElementException("Product not found"));
        }

        if (newCartItem.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity is required");
        } else {
            if (newCartItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }

        if (newCartItem.getPrice_at_time() == null) {
            throw new IllegalArgumentException("Price at time is required");
        } else {
            if (newCartItem.getPrice_at_time() <= 0) {
                throw new IllegalArgumentException("Price at time must be greater than 0");
            }
        }

        CartItem currentCartItem = this.cartItemRepository.findByProductAndCartAndDeletedAtIsNull(newCartItem.getProduct(), newCartItem.getCart());

        // if cart item is exist, update quantity
        if (currentCartItem != null) {
            currentCartItem.setQuantity(currentCartItem.getQuantity() + newCartItem.getQuantity());
            CartItem updatedCartItem = this.cartItemRepository.save(currentCartItem);
            return CartItemResponseDTO.builder()
                .id(updatedCartItem.getId())
                .quantity(updatedCartItem.getQuantity())
                .price_at_time(updatedCartItem.getPrice_at_time())
                .cart(CartItemCartDTO.builder()
                    .id(updatedCartItem.getCart().getId())
                    .build())
                .product(CartItemProductDTO.builder()
                    .id(updatedCartItem.getProduct().getId())
                    .build())
                .build();
        }

        CartItem createdCartItem = this.cartItemRepository.save(newCartItem);
        return CartItemResponseDTO.builder()
            .id(createdCartItem.getId())
            .quantity(createdCartItem.getQuantity())
            .price_at_time(createdCartItem.getPrice_at_time())
            .cart(CartItemCartDTO.builder()
                .id(createdCartItem.getCart().getId())
                .build())
            .product(CartItemProductDTO.builder()
                .id(createdCartItem.getProduct().getId())
                .build())
            .build();
    }

    @Override
    public CartItemResponseDTO handleUpdateCartItem(Long id, CartItem updateCartItem) {
        CartItem currentCartItem = this.cartItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart item not found"));

        if (updateCartItem.getQuantity() != null) {
            currentCartItem.setQuantity(updateCartItem.getQuantity());
        }

        CartItem updatedCartItem = this.cartItemRepository.save(currentCartItem);
        return CartItemResponseDTO.builder()
            .id(updatedCartItem.getId())
            .quantity(updatedCartItem.getQuantity())
            .price_at_time(updatedCartItem.getPrice_at_time())
            .cart(CartItemCartDTO.builder()
                .id(updatedCartItem.getCart().getId())
                .build())
            .product(CartItemProductDTO.builder()
                .id(updatedCartItem.getProduct().getId())
                .build())
            .build();
    }

    @Override
    public void handleDeleteCartItem(Long id) {
        CartItem currentCartItem = this.cartItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart item not found"));

        currentCartItem.setDeletedAt(Instant.now());
        this.cartItemRepository.save(currentCartItem);
    }

    @Override
    public List<CartItemResponseDTO> handleGetCartItemsByCartId(Long cartId) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(cartId).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        return this.cartItemRepository.findByCartAndDeletedAtIsNull(currentCart).stream().map(cartItem -> CartItemResponseDTO.builder()
            .id(cartItem.getId())
            .quantity(cartItem.getQuantity())
            .price_at_time(cartItem.getPrice_at_time())
            .cart(CartItemCartDTO.builder()
                .id(cartItem.getCart().getId())
                .build())
            .product(CartItemProductDTO.builder()
                .id(cartItem.getProduct().getId())
                .build())
            .build()).toList();
    }

    @Override
    public CartItemResponseDTO handleGetCartItemsByCartIdAndProductId(Long cartId, Long productId) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(cartId).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        Product currentProduct = this.productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        CartItem currentCartItem = this.cartItemRepository.findByProductAndCartAndDeletedAtIsNull(currentProduct, currentCart);
        
        return CartItemResponseDTO.builder()
            .id(currentCartItem.getId())
            .quantity(currentCartItem.getQuantity())
            .price_at_time(currentCartItem.getPrice_at_time())
            .cart(CartItemCartDTO.builder()
                .id(currentCartItem.getCart().getId())
                .build())
            .product(CartItemProductDTO.builder()
                .id(currentCartItem.getProduct().getId())
                .build())
            .build();
    }

    

    
}