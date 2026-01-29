package com.example.mini_ecom.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;


public interface ProductAssestService {
    public ProductAssest handleCreateProductAssest(ProductAssest newProductAssest);
    public ProductAssest handleGetProductAssestById(Long id);
    public ProductAssest handleUpdateProductAssest(Long id, ProductAssest updateProductAssest);
    public void handleDeleteProductAssest(Long id);

    public List<ProductAssest> handleGetProductAssestByProduct(Long productId);

    // public Page<ProductAssest> handleGetAllProductAssests(Pageable productAssestPageable);
}
