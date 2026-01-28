package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.repository.CategorieRepository;
import com.example.mini_ecom.repository.ProductRepository;
import com.example.mini_ecom.service.CategorieService;

import jakarta.transaction.Transactional;

@Service
public class CategorieServiceImpl implements CategorieService{
    private final CategorieRepository categorieRepository;
    private final ProductRepository productRepository;

    public CategorieServiceImpl(CategorieRepository categorieRepository, ProductRepository productRepository) {
        this.categorieRepository = categorieRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Categorie handleCreateCategorie(Categorie newCategorie) {
        if (this.categorieRepository.existsByNameAndDeletedAtIsNull(newCategorie.getName())) {
            throw new DuplicateKeyException("Name already exists");
        }
        return this.categorieRepository.save(newCategorie);
    }

    @Override
    public Categorie handleGetCategorieById(Long id) {
        return this.categorieRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));
    }

    @Override
    public Categorie handleGetCategorieByName(String name) {
        return this.categorieRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));
    }

    @Override
    public Categorie handleUpdateCategorie(Long id, Categorie updateCategorie) {
        if (this.categorieRepository.existsByNameAndDeletedAtIsNull(updateCategorie.getName())) {
            throw new DuplicateKeyException("Name already exists");
        }

        Categorie currnetCategorie = this.categorieRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));

        if (updateCategorie.getName() != null) {
            currnetCategorie.setName(updateCategorie.getName());
        }

        return this.categorieRepository.save(currnetCategorie);
    }

    @Override
    @Transactional
    public void handleDeleteCategorie(Long id) {
        Categorie currentCategorie = this.categorieRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));

        // List<Product> listProduct = 
        this.productRepository.findByCategorieAndDeletedAtIsNull(currentCategorie).stream().map(product -> {
            product.setCategorie(null);
            return product;
        }).toList();
        // this.productRepository.saveAll(listProduct);

        // this.categorieRepository.delete(currentCategorie);
        currentCategorie.setDeletedAt(Instant.now());
        this.categorieRepository.save(currentCategorie);
    }

    @Override
    public Page<Categorie> handleGetAllCategories(Pageable categoriePageable) {
        return this.categorieRepository.findAll(categoriePageable);
    }   
}
