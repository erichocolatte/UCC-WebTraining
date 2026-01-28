package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;
import com.example.mini_ecom.repository.CategorieRepository;
import com.example.mini_ecom.repository.ProductAssestRepository;
import com.example.mini_ecom.repository.ProductRepository;
import com.example.mini_ecom.service.ProductService;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategorieRepository categorieRepository;
    private final ProductAssestRepository productAssestRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategorieRepository categorieRepository, ProductAssestRepository productAssestRepository) {
        this.productRepository = productRepository;
        this.categorieRepository = categorieRepository;
        this.productAssestRepository = productAssestRepository;
    }

    @Override
    public Product handleCreateProduct(Product newProduct) {
        if (newProduct.getCategorie().getId() == null) {
            throw new IllegalArgumentException("Categorie id is null");
        }
        if (!this.categorieRepository.findById(newProduct.getCategorie().getId()).isPresent()) {
            throw new NoSuchElementException("Categorie not found");
        }

        return this.productRepository.save(newProduct);   
    }

    @Override
    public Product handleGetProductById(Long id) {
        return this.productRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));
    }

    @Override
    public Product handleUpdateProduct(Long id, Product updateProduct) {
        if (updateProduct.getCategorie().getId() != null) {
            if (!this.categorieRepository.findById(updateProduct.getCategorie().getId()).isPresent()) {
                throw new NoSuchElementException("Categorie not found");
            }

        }

        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        currentProduct.getCategorie().setId(updateProduct.getCategorie().getId());

        if (updateProduct.getName() != null) {
            currentProduct.setName(updateProduct.getName());
        }
        if (updateProduct.getDescription() != null) {
            currentProduct.setDescription(updateProduct.getDescription());
        }
        if (updateProduct.getPrice() != null) {
            currentProduct.setPrice(updateProduct.getPrice());
        }
        if (updateProduct.getStock() != null) {
            currentProduct.setStock(updateProduct.getStock());
        }
        if (updateProduct.getCategorie() != null) {
            currentProduct.setCategorie(updateProduct.getCategorie());
        }

        return this.productRepository.save(currentProduct);
    }

    @Override
    @Transactional
    public void handleDeleteProduct(Long id) {
        // this.productRepository.deleteById(id);
        Product currentProduct = this.productRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        // List<ProductAsset> listProductAsset = 
        this.productAssestRepository.findByProductAndDeletedAtIsNull(currentProduct).stream().map(productAsset -> {
            productAsset.setDeletedAt(Instant.now());
            return productAsset;
        }).toList();

        // this.productAssestRepository.saveAll(listProductAsset);

        // this.productRepository.delete(currentProduct);  
        currentProduct.setDeletedAt(Instant.now());
        this.productRepository.save(currentProduct);
    }

    @Override
    public Page<Product> handleGetAllProducts(Pageable productPageable) {
        return this.productRepository.findAll(productPageable);
    }

    // @Override
    // public void handleDeleteProductCategorie(Categorie currentCategorie) {
    //     this.productRepository.deleteByCategorie(currentCategorie);
    // }
}
