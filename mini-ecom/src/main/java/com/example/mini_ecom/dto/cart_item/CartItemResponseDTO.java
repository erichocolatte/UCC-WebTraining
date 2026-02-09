package com.example.mini_ecom.dto.cart_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {
    private Long id;
    private Integer quantity;
    private Double price_at_time;
    private CartItemCartDTO cart;
    private CartItemProductDTO product;
}
