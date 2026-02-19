package com.example.mini_ecom.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAssestDTO {
    private Long id;
    private String assest_url;
    private Boolean is_main;
}
