package com.example.mini_ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Optional<Product> findByNameAndDeletedAtIsNull(String name);
    public Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    public List<Product> findByCategorieAndDeletedAtIsNull(Categorie categorie);
    public Page<Product> findByCategorieAndDeletedAtIsNull(Categorie categorie, Pageable pageable);

        
}
