package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Cart;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.repository.CartItemRepository;
import com.example.mini_ecom.repository.CartRepository;
import com.example.mini_ecom.repository.UserRepository;
import com.example.mini_ecom.service.CartService;
import com.example.mini_ecom.util.SecurityUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Cart handleCreateCart(Cart newCart) {
        System.out.println(newCart.getUser().getId().toString());
        if (newCart.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));
        System.out.println(ownerId);

        if (!newCart.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        this.userRepository.findByIdAndDeletedAtIsNull(newCart.getUser().getId()).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        return this.cartRepository.save(newCart);
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Cart handleGetCartById(Long id) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentCart.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        return currentCart;
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Cart handleUpdateCart(Long id, Cart updateCart) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentCart.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        if (updateCart.getUser() != null) {
            this.userRepository.findByIdAndDeletedAtIsNull(updateCart.getUser().getId()).orElseThrow(() -> 
            new NoSuchElementException("User not found"));

            currentCart.getUser().setId(updateCart.getUser().getId());
        }

        if (updateCart.getStatus() != null) {
            currentCart.setStatus(updateCart.getStatus());
        }

        return this.cartRepository.save(currentCart);
    }

    @Override
    @Transactional
    // @PreAuthorize("hasRole('USER')")
    public void handleDeleteCart(Long id) {
        Cart currentCart = this.cartRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Cart not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentCart.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        this.cartItemRepository.findByCartAndDeletedAtIsNull(currentCart).stream().map(cartItem -> {
            cartItem.setDeletedAt(Instant.now());
            // this.cartItemRepository.save(cartItem);
            return cartItem;
        }).toList();

        currentCart.setDeletedAt(Instant.now());
        this.cartRepository.save(currentCart);
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public List<Cart> handleGetAllCartsByUserId(Long userId) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        // User ownUser = SecurityUtil.getCurrentUserLogin().get();
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentUser.getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        return this.cartRepository.findByUserAndDeletedAtIsNull(currentUser);
    }
}
