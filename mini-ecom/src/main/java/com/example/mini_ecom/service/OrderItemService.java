package com.example.mini_ecom.service;

import java.util.List;

import com.example.mini_ecom.dto.order_item.OrderItemResponseDTO;
import com.example.mini_ecom.model.OrderItem;

public interface OrderItemService {
    public List<OrderItemResponseDTO> handleGetAllOrdersByOrderId(Long orderId);

    public OrderItemResponseDTO handleGetOrderItemById(Long id);

    public OrderItemResponseDTO handleCreateOrderItem(OrderItem newOrderItem);

    public OrderItemResponseDTO handleUpdateOrderItem(Long id, OrderItem updateOrderItem);

    public void handleDeleteOrderItem(Long id);
}
