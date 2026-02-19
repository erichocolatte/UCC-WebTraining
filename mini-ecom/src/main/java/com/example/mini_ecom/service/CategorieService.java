package com.example.mini_ecom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.dto.categorie.CategorieResponseDTO;
import com.example.mini_ecom.model.Categorie;

public interface CategorieService {
    public CategorieResponseDTO handleCreateCategorie(Categorie categorie);
    public CategorieResponseDTO handleGetCategorieById(Long id);
    public CategorieResponseDTO handleGetCategorieByName(String name);
    public CategorieResponseDTO handleUpdateCategorie(Long id, Categorie updateCategorie);
    public void handleDeleteCategorie(Long id);

    public PaginationResponseDTO<CategorieResponseDTO, MetaDTO> handleGetAllCategories(Pageable categoriePageable);
    
}
