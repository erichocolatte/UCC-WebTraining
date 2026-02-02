package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.model.CartItem;

public interface CartItemService {
    public CartItem handleCreateCartItem(CartItem newCartItem);
    public CartItem handleUpdateCartItem(Long id, CartItem updatedCartItem);
    public void handleDeleteCartItem(Long id);

    public List<CartItem> handleGetCartItemsByCartId(Long cartId);
    public CartItem handleGetCartItemsByCartIdAndProductId(Long cartId, Long productId);
}
