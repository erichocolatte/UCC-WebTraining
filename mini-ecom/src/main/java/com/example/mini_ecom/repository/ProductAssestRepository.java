package com.example.mini_ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;

@Repository
public interface ProductAssestRepository extends JpaRepository<ProductAssest, Long> {
    public List<ProductAssest> findByProductAndDeletedAtIsNull(Product product);
}
