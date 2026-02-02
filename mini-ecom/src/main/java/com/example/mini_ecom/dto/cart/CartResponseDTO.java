package com.example.mini_ecom.dto.cart;

import com.example.mini_ecom.util.constants.CartStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
    private Long id;
    private CartStatusEnum status;
    
    private CartUserDTO user;
    
}
