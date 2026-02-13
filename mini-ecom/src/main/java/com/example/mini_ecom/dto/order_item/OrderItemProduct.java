package com.example.mini_ecom.dto.order_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemProduct {
    private Long id;
    private String name;
    
}
