package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Order;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.repository.OrderItemRepository;
import com.example.mini_ecom.repository.OrderRepository;
import com.example.mini_ecom.repository.UserRepository;
import com.example.mini_ecom.service.OrderService;
import com.example.mini_ecom.util.SecurityUtil;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Order handleCreateOrder(Order newOrder) {
        if (newOrder.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!newOrder.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        this.userRepository.findByIdAndDeletedAtIsNull(newOrder.getUser().getId()).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        return this.orderRepository.save(newOrder);
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Order handleGetOrderById(Long id) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentOrder.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        return currentOrder;
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public Order handleUpdateOrder(Long id, Order updateOrder) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));
        
        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentOrder.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }
        
        if (updateOrder.getUser() != null) {
            this.userRepository.findByIdAndDeletedAtIsNull(updateOrder.getUser().getId()).orElseThrow(() -> 
            new NoSuchElementException("User not found")
            );
            // updateOrder.getUser().setId(updat);
            currentOrder.getUser().setId(updateOrder.getUser().getId());
        } 

        if (updateOrder.getTotal_price() != null) {
            currentOrder.setTotal_price(updateOrder.getTotal_price());
        }

        if (updateOrder.getStatus() != null) {
            currentOrder.setStatus(updateOrder.getStatus());
        }

        return this.orderRepository.save(currentOrder);
    }

    @Override
    @Transactional
    // @PreAuthorize("hasRole('USER')")
    public void handleDeleteOrder(Long id) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentOrder.getUser().getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        this.orderItemRepository.findByOrderAndDeletedAtIsNull(currentOrder).stream().map(orderItem -> {
            orderItem.setDeletedAt(Instant.now());
            return orderItem;
        }).toList();

        currentOrder.setDeletedAt(Instant.now());
        this.orderRepository.save(currentOrder);
    }

    @Override
    // @PreAuthorize("hasRole('USER')")
    public List<Order> handleGetAllOrdersByUserId(Long userId) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        String ownerId = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentUser.getId().toString().equals(ownerId)) {
            throw new AccessDeniedException("Permission denied");
        }

        return this.orderRepository.findByUserAndDeletedAtIsNull(currentUser);
    }


}
