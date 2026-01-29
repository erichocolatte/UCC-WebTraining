package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;
import com.example.mini_ecom.repository.ProductAssestRepository;
import com.example.mini_ecom.repository.ProductRepository;
import com.example.mini_ecom.service.ProductAssestService;

import jakarta.transaction.Transactional;

@Service
public class ProductAssestServiceImpl implements ProductAssestService {
    private final ProductAssestRepository productAssestRepository;
    private final ProductRepository productRepository;

    public ProductAssestServiceImpl(ProductAssestRepository productAssestRepository, ProductRepository productRepository) {
        this.productAssestRepository = productAssestRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductAssest handleCreateProductAssest(ProductAssest newProductAssest) {
        if (newProductAssest.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product is null");
        }
        return this.productAssestRepository.save(newProductAssest);
    }

    @Override
    public ProductAssest handleGetProductAssestById(Long id) {
        return this.productAssestRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("ProductAssest not found"));
    }

    @Override
    public ProductAssest handleUpdateProductAssest(Long id, ProductAssest updateProductAssest) {
        ProductAssest currentProductAssest = this.productAssestRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("ProductAssest not found"));
        
        if (updateProductAssest.getProduct() != null) {
            if(updateProductAssest.getProduct().getId() != null) {
                this.productRepository.findById(updateProductAssest.getProduct().getId()).orElseThrow(() -> 
                new NoSuchElementException("Product not found"));

                currentProductAssest.getProduct().setId(updateProductAssest.getProduct().getId());
            }
        }

        if(updateProductAssest.getIs_main() != null) {
            currentProductAssest.setIs_main(updateProductAssest.getIs_main());
        }

        if(updateProductAssest.getAssest_url() != null) {
            currentProductAssest.setAssest_url(updateProductAssest.getAssest_url());
        }

        return this.productAssestRepository.save(currentProductAssest);
    }

    @Override
    @Transactional
    public void handleDeleteProductAssest(Long id) {
        ProductAssest currentProductAssest = this.productAssestRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("ProductAssest not found"));

        currentProductAssest.setDeletedAt(Instant.now());
        this.productAssestRepository.save(currentProductAssest);
    }

    @Override
    public List<ProductAssest> handleGetProductAssestByProduct(Long productId) {
        Product currentProduct = this.productRepository.findById(productId).orElseThrow(() -> 
            new NoSuchElementException("Product not found")
        );
        return this.productAssestRepository.findByProductAndDeletedAtIsNull(currentProduct);
    }
    



}
