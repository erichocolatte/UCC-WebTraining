package com.example.mini_ecom.dto.order;

import com.example.mini_ecom.util.constants.OrderStatusEnum;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Double total_price;
    private OrderStatusEnum status;

    private OrderUserDTO user;
}
