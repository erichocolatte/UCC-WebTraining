package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.dto.cart_item.CartItemResponseDTO;
import com.example.mini_ecom.model.CartItem;

public interface CartItemService {
    public CartItemResponseDTO handleCreateCartItem(CartItem newCartItem);
    public CartItemResponseDTO handleUpdateCartItem(Long id, CartItem updatedCartItem);
    public void handleDeleteCartItem(Long id);

    public List<CartItemResponseDTO> handleGetCartItemsByCartId(Long cartId);
    public CartItemResponseDTO handleGetCartItemsByCartIdAndProductId(Long cartId, Long productId);
}
