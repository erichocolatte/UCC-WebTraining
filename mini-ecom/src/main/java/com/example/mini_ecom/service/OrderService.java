package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.dto.order.OrderResponseDTO;
import com.example.mini_ecom.model.Order;

public interface OrderService {
    public OrderResponseDTO handleCreateOrder(Order order);
    public OrderResponseDTO handleGetOrderById(Long id);
    public OrderResponseDTO handleUpdateOrder(Long id, Order order);
    public void handleDeleteOrder(Long id);
    // public Page<Order> handleGetAllOrders(Pageable orderPageable);
    public List<OrderResponseDTO> handleGetAllOrdersByUserId(Long userId);
}
