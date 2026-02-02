package com.example.mini_ecom.controller;

import java.time.Instant;
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
import com.example.mini_ecom.dto.order.OrderResponseDTO;
import com.example.mini_ecom.dto.order.OrderUserDTO;
import com.example.mini_ecom.model.Order;
import com.example.mini_ecom.service.OrderService;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<ApiResponseDTO<?>> getAllOrdersByUser(
        @PathVariable("userId") Long id
    ) {
        List<OrderResponseDTO> listOrderResponseDTO = this.orderService.handleGetAllOrdersByUserId(id).stream().map(order -> {
            return OrderResponseDTO.builder()
                .total_price(order.getTotal_price())
                .status(order.getStatus())
                .user(OrderUserDTO.builder()
                    .id(order.getUser().getId())
                    .build())
                .build();
        }).toList();

        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Get all orders successfully")
                .build()
            )
            .data(listOrderResponseDTO)
            .timeStamp(Instant.now())
            .build()
        );
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponseDTO<?>> createOrder(
        @RequestBody Order newOrder
    ) {
        Order createdOrder = this.orderService.handleCreateOrder(newOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Order created successfully")
                .build()
            )
            .data(OrderResponseDTO.builder()
                .total_price(createdOrder.getTotal_price())
                .status(createdOrder.getStatus())
                .user(OrderUserDTO.builder()
                    .id(createdOrder.getUser().getId())
                    .build())
                .build())
            .timeStamp(Instant.now())
            .build()
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getOrder(
        @PathVariable("id") Long id
    ) {
        Order currentOrder = this.orderService.handleGetOrderById(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Get order successfully")
                .build()
            )
            .data(OrderResponseDTO.builder()
                .total_price(currentOrder.getTotal_price())
                .status(currentOrder.getStatus())
                .user(OrderUserDTO.builder()
                    .id(currentOrder.getUser().getId())
                    .build())
                .build())
            .timeStamp(Instant.now())
            .build()
        );
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateOrder(
        @PathVariable("id") Long id,
        @RequestBody Order updateOrder
    ) {
        Order updatedOrder = this.orderService.handleUpdateOrder(id, updateOrder);
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order updated successfully")
                .build()
            )
            .data(OrderResponseDTO.builder()
                .total_price(updatedOrder.getTotal_price())
                .status(updatedOrder.getStatus())
                .user(OrderUserDTO.builder()
                    .id(updatedOrder.getUser().getId())
                    .build())
                .build())
            .timeStamp(Instant.now())
            .build()
        );
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteOrder(
        @PathVariable("id") Long id
    ) {
        this.orderService.handleDeleteOrder(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Order deleted successfully")
                .build()
            )
            .timeStamp(Instant.now())
            .build()
        );
    }
}
