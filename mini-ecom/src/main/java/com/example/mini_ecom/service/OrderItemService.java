package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.model.OrderItem;

public interface OrderItemService {
    public List<OrderItem> handleGetAllOrdersByOrderId(Long orderId);

    public OrderItem handleGetOrderItemById(Long id);

    public OrderItem handleCreateOrderItem(OrderItem newOrderItem);

    public OrderItem handleUpdateOrderItem(Long id, OrderItem updateOrderItem);

    public void handleDeleteOrderItem(Long id);
}
