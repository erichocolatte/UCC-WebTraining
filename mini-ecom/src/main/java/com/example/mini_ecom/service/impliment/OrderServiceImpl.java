package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Order;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.repository.OrderItemRepository;
import com.example.mini_ecom.repository.OrderRepository;
import com.example.mini_ecom.repository.UserRepository;
import com.example.mini_ecom.service.OrderService;
import com.example.mini_ecom.util.SecurityUtil;

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
    public Order handleCreateOrder(Order newOrder) {
        if (newOrder.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        this.userRepository.findByIdAndDeletedAtIsNull(newOrder.getUser().getId()).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        return this.orderRepository.save(newOrder);
    }

    @Override
    public Order handleGetOrderById(Long id) {
        return this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));
    }

    @Override
    public Order handleUpdateOrder(Long id, Order updateOrder) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));
        
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
    public void handleDeleteOrder(Long id) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));

        this.orderItemRepository.findByOrderAndDeletedAtIsNull(currentOrder).stream().map(orderItem -> {
            orderItem.setDeletedAt(Instant.now());
            return orderItem;
        }).toList();

        currentOrder.setDeletedAt(Instant.now());
        this.orderRepository.save(currentOrder);
    }

    @Override
    public List<Order> handleGetAllOrdersByUserId(Long userId) {
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        String ownerName = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> 
        new AccessDeniedException("Can't get auth user"));

        if (!currentUser.getName().equals(ownerName)) {
            throw new AccessDeniedException("Permission denied");
        }

        return this.orderRepository.findByUserAndDeletedAtIsNull(currentUser);
    }


}
