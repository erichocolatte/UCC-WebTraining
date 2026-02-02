package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

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
    public CartItem handleCreateCartItem(CartItem newCartItem) {
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
            return this.cartItemRepository.save(currentCartItem);
        }

        return this.cartItemRepository.save(newCartItem);
    }

    @Override
    public CartItem handleUpdateCartItem(Long id, CartItem updateCartItem) {
        CartItem currentCartItem = this.cartItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart item not found"));

        if (updateCartItem.getQuantity() != null) {
            currentCartItem.setQuantity(updateCartItem.getQuantity());
        }

        return this.cartItemRepository.save(currentCartItem);
    }

    @Override
    public void handleDeleteCartItem(Long id) {
        CartItem currentCartItem = this.cartItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart item not found"));

        currentCartItem.setDeletedAt(Instant.now());
        this.cartItemRepository.save(currentCartItem);
    }

    @Override
    public List<CartItem> handleGetCartItemsByCartId(Long cartId) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(cartId).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        return this.cartItemRepository.findByCartAndDeletedAtIsNull(currentCart);
    }

    @Override
    public CartItem handleGetCartItemsByCartIdAndProductId(Long cartId, Long productId) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(cartId).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        Product currentProduct = this.productRepository.findByIdAndDeletedAtIsNull(productId).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        return this.cartItemRepository.findByProductAndCartAndDeletedAtIsNull(currentProduct, currentCart);
    }

    

    
}