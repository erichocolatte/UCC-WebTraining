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
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Order item created successfully")
                .build()
            )
            .data(this.orderItemService.handleCreateOrderItem(newOrderItem))
            .build()
        );
    }

    @GetMapping("/order-items/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getOrderItemById(
        @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order item fetched successfully")
                .build()
            )
            .data(this.orderItemService.handleGetOrderItemById(id))
            .build()
        );
    }

    @PutMapping("/order-items/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateOrderItem(
        @PathVariable Long id,
        @Valid @RequestBody OrderItem updateOrderItem
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order item updated successfully")
                .build()
            )
            .data(this.orderItemService.handleUpdateOrderItem(id, updateOrderItem))
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
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order items fetched successfully")
                .build()
            )
            .data(this.orderItemService.handleGetAllOrdersByOrderId(orderId))
            .build()
        );
    }
}
