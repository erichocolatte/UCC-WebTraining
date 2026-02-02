package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.model.Order;

public interface OrderService {
    public Order handleCreateOrder(Order order);
    public Order handleGetOrderById(Long id);
    public Order handleUpdateOrder(Long id, Order order);
    public void handleDeleteOrder(Long id);
    // public Page<Order> handleGetAllOrders(Pageable orderPageable);
    public List<Order> handleGetAllOrdersByUserId(Long userId);
}
