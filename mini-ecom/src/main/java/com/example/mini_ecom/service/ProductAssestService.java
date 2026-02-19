package com.example.mini_ecom.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.dto.product_assest.ProductAssestResponseDTO;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;


public interface ProductAssestService {
    public ProductAssestResponseDTO handleCreateProductAssest(ProductAssest newProductAssest);
    public ProductAssestResponseDTO handleGetProductAssestById(Long id);
    public ProductAssestResponseDTO handleUpdateProductAssest(Long id, ProductAssest updateProductAssest);
    public void handleDeleteProductAssest(Long id);

    public List<ProductAssestResponseDTO> handleGetProductAssestByProduct(Long productId);

    // public Page<ProductAssest> handleGetAllProductAssests(Pageable productAssestPageable);
}
