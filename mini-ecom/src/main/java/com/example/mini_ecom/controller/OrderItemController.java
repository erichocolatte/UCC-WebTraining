package com.example.mini_ecom.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mini_ecom.dto.ApiResponseDTO;
import com.example.mini_ecom.dto.order_item.OrderItemOrder;
import com.example.mini_ecom.dto.order_item.OrderItemProduct;
import com.example.mini_ecom.dto.order_item.OrderItemResponseDTO;
import com.example.mini_ecom.model.OrderItem;
import com.example.mini_ecom.service.OrderItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/order-items")
    public ResponseEntity<ApiResponseDTO<?>> createOrderItem(
        @Valid @RequestBody OrderItem newOrderItem
    ) {
        OrderItem createdOrderItem = this.orderItemService.handleCreateOrderItem(newOrderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Order item created successfully")
                .build()
            )
            .data(OrderItemResponseDTO.builder()
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
                .build())
            .build()
        );
    }

    @GetMapping("/order-items/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getOrderItemById(
        @PathVariable Long id
    ) {
        OrderItem orderItem = this.orderItemService.handleGetOrderItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order item fetched successfully")
                .build()
            )
            .data(OrderItemResponseDTO.builder()
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
                .build())
            .build()
        );
    }

    @PutMapping("/order-items/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateOrderItem(
        @PathVariable Long id,
        @Valid @RequestBody OrderItem updateOrderItem
    ) {
        OrderItem updatedOrderItem = this.orderItemService.handleUpdateOrderItem(id, updateOrderItem);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order item updated successfully")
                .build()
            )
            .data(OrderItemResponseDTO.builder()
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
                .build())
            .build()
        );
    }

    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteOrderItem(
        @PathVariable Long id
    ) {
        this.orderItemService.handleDeleteOrderItem(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order item deleted successfully")
                .build()
            )
            .build()
        );
    }

    @GetMapping("/order-items/orders/{orderId}")
    public ResponseEntity<ApiResponseDTO<?>> getOrderItemsByOrderId(
        @PathVariable Long orderId
    ) {
        List<OrderItem> orderItems = this.orderItemService.handleGetAllOrdersByOrderId(orderId);
        List<OrderItemResponseDTO> orderItemResponseDTOs = orderItems.stream().map(orderItem -> OrderItemResponseDTO.builder()
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
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order items fetched successfully")
                .build()
            )
            .data(orderItemResponseDTOs)
            .build()
        );
    }
}
