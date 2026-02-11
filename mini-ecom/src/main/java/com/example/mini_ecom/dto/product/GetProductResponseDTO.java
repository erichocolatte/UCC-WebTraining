package com.example.mini_ecom.dto.product;

import java.util.List;

import com.example.mini_ecom.dto.product_assest.GetProductAssestResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;

    private ProductCategorieDTO categorie;
    private List<GetProductAssestResponseDTO> assets;
}
