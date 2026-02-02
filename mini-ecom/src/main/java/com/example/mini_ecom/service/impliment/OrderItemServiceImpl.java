package com.example.mini_ecom.service.impliment;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Order;
import com.example.mini_ecom.model.OrderItem;
import com.example.mini_ecom.repository.OrderItemRepository;
import com.example.mini_ecom.repository.OrderRepository;
import com.example.mini_ecom.repository.ProductRepository;
import com.example.mini_ecom.service.OrderItemService;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderItem handleCreateOrderItem(OrderItem newOrderItem) {
        if (newOrderItem.getOrder() == null) {
            throw new IllegalArgumentException("Order is required");
        } else {
            this.orderRepository.findByIdAndDeletedAtIsNull(newOrderItem.getOrder().getId()).orElseThrow(() -> 
            new IllegalArgumentException("Order not found"));
        }

        if (newOrderItem.getProduct() == null) {
            throw new IllegalArgumentException("Product is required");
        } else {
            this.productRepository.findByIdAndDeletedAtIsNull(newOrderItem.getProduct().getId()).orElseThrow(() -> 
            new IllegalArgumentException("Product not found"));
        }

        if (newOrderItem.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity is required");
        }

        if (newOrderItem.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }

        return this.orderItemRepository.save(newOrderItem);
    }

    @Override
    public OrderItem handleGetOrderItemById(Long id) {
        return this.orderItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order item not found"));
    }

    @Override
    public OrderItem handleUpdateOrderItem(Long id, OrderItem updateOrderItem) {
        OrderItem currnetOrderItem = this.orderItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order item not found"));

        if (updateOrderItem.getProduct() != null) {
            this.productRepository.findByIdAndDeletedAtIsNull(updateOrderItem.getProduct().getId()).orElseThrow(() -> 
            new NoSuchElementException("Product not found"));
            currnetOrderItem.getProduct().setId(updateOrderItem.getProduct().getId());
        }

        if (updateOrderItem.getOrder() != null) {
            this.orderRepository.findByIdAndDeletedAtIsNull(updateOrderItem.getOrder().getId()).orElseThrow(() -> 
            new NoSuchElementException("Order not found"));
            currnetOrderItem.getOrder().setId(updateOrderItem.getOrder().getId());
        }

        if (updateOrderItem.getQuantity() != null) {
            currnetOrderItem.setQuantity(updateOrderItem.getQuantity());
        }

        if (updateOrderItem.getPrice() != null) {
            currnetOrderItem.setPrice(updateOrderItem.getPrice());
        }

        return this.orderItemRepository.save(currnetOrderItem);

    }

    @Override
    public void handleDeleteOrderItem(Long id) {
        OrderItem currentOrderItem = this.orderItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order item not found"));
        this.orderItemRepository.delete(currentOrderItem);
    }

    public List<OrderItem> handleGetAllOrdersByOrderId(Long orderId) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(orderId).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));

        return this.orderItemRepository.findByOrderAndDeletedAtIsNull(currentOrder);
    }


    
}
