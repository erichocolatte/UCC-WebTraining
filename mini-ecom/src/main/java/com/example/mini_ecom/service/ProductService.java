package com.example.mini_ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.model.Product;

public interface ProductService {
    public Product handleCreateProduct(Product newProduct);
    public Product handleGetProductById(Long id);
    public Product handleUpdateProduct(Long id, Product product);

    public void handleDeleteProduct(Long id);   

    public Page<Product> handleGetAllProducts(Pageable productPageable);
    // public void handleDeleteProductCategorie(Categorie currentCategorie);
    
}
