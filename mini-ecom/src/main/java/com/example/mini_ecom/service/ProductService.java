package com.example.mini_ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.dto.product.ProductResponseDTO;
import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.model.Product;

public interface ProductService {
    public ProductResponseDTO handleCreateProduct(Product newProduct);
    public ProductResponseDTO handleGetProductById(Long id);
    public ProductResponseDTO handleUpdateProduct(Long id, Product product);

    public void handleDeleteProduct(Long id);   

    public PaginationResponseDTO<ProductResponseDTO,MetaDTO> handleGetAllProducts(Pageable productPageable);

    public PaginationResponseDTO<ProductResponseDTO,MetaDTO> handleGetAllProductsByCategory(Long categorieId, Pageable productPageable);
    // public void handleDeleteProductCategorie(Categorie currentCategorie);
    
}
