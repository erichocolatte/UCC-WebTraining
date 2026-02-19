package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.dto.cart.CartResponseDTO;
import com.example.mini_ecom.model.Cart;

public interface CartService {
    public CartResponseDTO handleCreateCart(Cart cart);
    public CartResponseDTO handleGetCartById(Long id);
    public CartResponseDTO handleUpdateCart(Long id, Cart cart);
    public void handleDeleteCart(Long id);
    // public Page<Cart> handleGetAllCarts(Pageable cartPageable);
    public List<CartResponseDTO> handleGetAllCartsByUserId(Long userId);
    public CartResponseDTO handleGetActiveCartByUserId(Long userId);
}
