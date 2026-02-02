package com.example.mini_ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByUserIdAndDeletedAtIsNull(Long userId);

    public Optional<Order> findByIdAndDeletedAtIsNull(Long id);
}
