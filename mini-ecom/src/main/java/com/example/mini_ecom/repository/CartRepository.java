package com.example.mini_ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Cart;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.util.constants.CartStatusEnum;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    public List<Cart> findByUserAndDeletedAtIsNull(User user);
    
    public Optional<Cart> findByIdAndDeletedAtIsNull(Long id);

    public Optional<Cart> findByUserAndStatusAndDeletedAtIsNull(User user, CartStatusEnum status);

    public Page<Cart> findAllByDeletedAtIsNull(Pageable pageable);
    // public Boolean (Long id, User user);
}
