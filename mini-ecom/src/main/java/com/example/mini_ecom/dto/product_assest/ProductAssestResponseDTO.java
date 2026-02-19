package com.example.mini_ecom.dto.product_assest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAssestResponseDTO {
    private Long id;
    private String assest_url;
    private Boolean is_main;

    private ProductAssestProduct product;
}
