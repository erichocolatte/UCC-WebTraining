package com.example.mini_ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Cart;
import com.example.mini_ecom.model.CartItem;
import com.example.mini_ecom.model.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    public List<CartItem> findByCartAndDeletedAtIsNull(Cart cart);
    public CartItem findByProductAndCartAndDeletedAtIsNull(Product product, Cart cart);
    public List<CartItem> findByProductAndDeletedAtIsNull(Product product);
    public Optional<CartItem> findByIdAndDeletedAtIsNull(Long id);
}
