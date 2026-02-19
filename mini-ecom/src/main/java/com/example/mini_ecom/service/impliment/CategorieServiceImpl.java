package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.dto.categorie.CategorieResponseDTO;
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
    @CacheEvict(value = "categories")
    public CategorieResponseDTO handleCreateCategorie(Categorie newCategorie) {
        if (this.categorieRepository.existsByNameAndDeletedAtIsNull(newCategorie.getName())) {
            throw new DuplicateKeyException("Name already exists");
        }
        Categorie createdCategorie = this.categorieRepository.save(newCategorie);
        return CategorieResponseDTO.builder()
            .id(createdCategorie.getId())
            .name(createdCategorie.getName())
            .build();
    }

    @Override
    public CategorieResponseDTO handleGetCategorieById(Long id) {
        Categorie currentCategorie = this.categorieRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));

        return CategorieResponseDTO.builder()
            .id(currentCategorie.getId())
            .name(currentCategorie.getName())
            .build();
    }

    @Override
    public CategorieResponseDTO handleGetCategorieByName(String name) {
        Categorie currentCategorie = this.categorieRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));

        return CategorieResponseDTO.builder()
            .id(currentCategorie.getId())
            .name(currentCategorie.getName())
            .build();
    }

    @Override
    @CacheEvict(value = "categories")
    public CategorieResponseDTO handleUpdateCategorie(Long id, Categorie updateCategorie) {
        if (this.categorieRepository.existsByNameAndDeletedAtIsNull(updateCategorie.getName())) {
            throw new DuplicateKeyException("Name already exists");
        }

        Categorie currnetCategorie = this.categorieRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Categorie not found"));

        if (updateCategorie.getName() != null) {
            currnetCategorie.setName(updateCategorie.getName());
        }

        Categorie updatedCategorie = this.categorieRepository.save(currnetCategorie);
        return CategorieResponseDTO.builder()
            .id(updatedCategorie.getId())
            .name(updatedCategorie.getName())
            .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories")
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
    @Cacheable(value = "categories")
    public PaginationResponseDTO<CategorieResponseDTO, MetaDTO> handleGetAllCategories(Pageable categoriePageable) {
        Page<Categorie> currentPage = this.categorieRepository.findAllByDeletedAtIsNull(categoriePageable);

        List<CategorieResponseDTO> list = currentPage.getContent()
        .stream().map(categorie -> 
            CategorieResponseDTO.builder()
            .id(categorie.getId())
            .name(categorie.getName())
            .build()
        ).toList();

        return PaginationResponseDTO.<CategorieResponseDTO, MetaDTO>builder()
            .result(list)
            .meta(MetaDTO.builder()
                .page(currentPage.getNumber())
                .pageSize(currentPage.getSize())
                .pages(currentPage.getTotalPages())
                .total(currentPage.getTotalElements())
                .build())
            .build();
    }   
}
