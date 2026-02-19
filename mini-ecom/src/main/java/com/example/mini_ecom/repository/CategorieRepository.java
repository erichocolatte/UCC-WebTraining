package com.example.mini_ecom.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    public Optional<Categorie> findByIdAndDeletedAtIsNull(Long id);
    public Optional<Categorie> findByNameAndDeletedAtIsNull(String name);
    public Boolean existsByNameAndDeletedAtIsNull(String name);

    public Page<Categorie> findAllByDeletedAtIsNull(Pageable pageable);
}
