package com.example.mini_ecom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.model.Categorie;

public interface CategorieService {
    public Categorie handleCreateCategorie(Categorie categorie);
    public Categorie handleGetCategorieById(Long id);
    public Categorie handleGetCategorieByName(String name);
    public Categorie handleUpdateCategorie(Long id, Categorie updateCategorie);
    public void handleDeleteCategorie(Long id);

    public Page<Categorie> handleGetAllCategories(Pageable categoriePageable);
    
}
