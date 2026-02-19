package com.example.mini_ecom.service.impliment;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.order_item.OrderItemOrder;
import com.example.mini_ecom.dto.order_item.OrderItemProduct;
import com.example.mini_ecom.dto.order_item.OrderItemResponseDTO;
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
    public OrderItemResponseDTO handleCreateOrderItem(OrderItem newOrderItem) {
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

        OrderItem createdOrderItem = this.orderItemRepository.save(newOrderItem);
        return OrderItemResponseDTO.builder()
            .id(createdOrderItem.getId())
            .order(OrderItemOrder.builder()
                .id(createdOrderItem.getOrder().getId())
                .build())
            .product(OrderItemProduct.builder()
                .id(createdOrderItem.getProduct().getId())
                .name(createdOrderItem.getProduct().getName())
                .build())
            .quantity(createdOrderItem.getQuantity())
            .price(createdOrderItem.getPrice())
            .build();
    }

    @Override
    public OrderItemResponseDTO handleGetOrderItemById(Long id) {
        OrderItem currentOrderItem = this.orderItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order item not found"));
        return OrderItemResponseDTO.builder()
            .id(currentOrderItem.getId())
            .order(OrderItemOrder.builder()
                .id(currentOrderItem.getOrder().getId())
                .build())
            .product(OrderItemProduct.builder()
                .id(currentOrderItem.getProduct().getId())
                .name(currentOrderItem.getProduct().getName())
                .build())
            .quantity(currentOrderItem.getQuantity())
            .price(currentOrderItem.getPrice())
            .build();
    }

    @Override
    public OrderItemResponseDTO handleUpdateOrderItem(Long id, OrderItem updateOrderItem) {
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

        OrderItem updatedOrderItem = this.orderItemRepository.save(currnetOrderItem);
        return OrderItemResponseDTO.builder()
            .id(updatedOrderItem.getId())
            .order(OrderItemOrder.builder()
                .id(updatedOrderItem.getOrder().getId())
                .build())
            .product(OrderItemProduct.builder()
                .id(updatedOrderItem.getProduct().getId())
                .name(updatedOrderItem.getProduct().getName())
                .build())
            .quantity(updatedOrderItem.getQuantity())
            .price(updatedOrderItem.getPrice())
            .build();

    }

    @Override
    public void handleDeleteOrderItem(Long id) {
        OrderItem currentOrderItem = this.orderItemRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Order item not found"));
        this.orderItemRepository.delete(currentOrderItem);
    }

    public List<OrderItemResponseDTO> handleGetAllOrdersByOrderId(Long orderId) {
        Order currentOrder = this.orderRepository.findByIdAndDeletedAtIsNull(orderId).orElseThrow(() -> 
        new NoSuchElementException("Order not found"));

        return this.orderItemRepository.findByOrderAndDeletedAtIsNull(currentOrder).stream().map(orderItem -> OrderItemResponseDTO.builder()
            .id(orderItem.getId())
            .order(OrderItemOrder.builder()
                .id(orderItem.getOrder().getId())
                .build())
            .product(OrderItemProduct.builder()
                .id(orderItem.getProduct().getId())
                .name(orderItem.getProduct().getName())
                .build())
            .quantity(orderItem.getQuantity())
            .price(orderItem.getPrice())
            .build()).toList();
    }


    
}
