package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.model.Cart;

public interface CartService {
    public Cart handleCreateCart(Cart cart);
    public Cart handleGetCartById(Long id);
    public Cart handleUpdateCart(Long id, Cart cart);
    public void handleDeleteCart(Long id);
    // public Page<Cart> handleGetAllCarts(Pageable cartPageable);
    public List<Cart> handleGetAllCartsByUserId(Long userId);
    public Cart handleGetActiveCartByUserId(Long userId);
}
