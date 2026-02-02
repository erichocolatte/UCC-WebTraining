package com.example.mini_ecom.dto.order_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private Long id;
    private OrderItemOrder order;
    private OrderItemProduct product;
    private Integer quantity;
    private Double price;
}
