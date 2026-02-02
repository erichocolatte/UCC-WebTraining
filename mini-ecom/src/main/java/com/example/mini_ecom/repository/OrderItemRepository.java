package com.example.mini_ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Order;
import com.example.mini_ecom.model.OrderItem;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.User;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    public OrderItem findByProductAndOrderAndDeletedAtIsNull(Product product, Order order);

    public List<OrderItem> findByOrderAndDeletedAtIsNull(Order order);

    public List<OrderItem> findByProductAndDeletedAtIsNull(Product product);

    public Optional<OrderItem> findByIdAndDeletedAtIsNull(Long id);
}
