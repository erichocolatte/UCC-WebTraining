package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.product_assest.ProductAssestProduct;
import com.example.mini_ecom.dto.product_assest.ProductAssestResponseDTO;
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
    public ProductAssestResponseDTO handleCreateProductAssest(ProductAssest newProductAssest) {
        if (newProductAssest.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product is null");
        }
        ProductAssest createdProductAssest = this.productAssestRepository.save(newProductAssest);
        return ProductAssestResponseDTO.builder()
            .id(createdProductAssest.getId())
            .assest_url(createdProductAssest.getAssest_url())
            .is_main(createdProductAssest.getIs_main())
            .product(ProductAssestProduct.builder()
                .id(createdProductAssest.getProduct().getId())
                .build())
            .build();
    }

    @Override
    public ProductAssestResponseDTO handleGetProductAssestById(Long id) {
        ProductAssest currentProductAssest = this.productAssestRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("ProductAssest not found"));
        return ProductAssestResponseDTO.builder()
            .id(currentProductAssest.getId())
            .assest_url(currentProductAssest.getAssest_url())
            .is_main(currentProductAssest.getIs_main())
            .product(ProductAssestProduct.builder()
                .id(currentProductAssest.getProduct().getId())
                .build())
            .build();
    }

    @Override
    public ProductAssestResponseDTO handleUpdateProductAssest(Long id, ProductAssest updateProductAssest) {
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

        ProductAssest updatedProductAssest = this.productAssestRepository.save(currentProductAssest);
        return ProductAssestResponseDTO.builder()
            .id(updatedProductAssest.getId())
            .assest_url(updatedProductAssest.getAssest_url())
            .is_main(updatedProductAssest.getIs_main())
            .product(ProductAssestProduct.builder()
                .id(updatedProductAssest.getProduct().getId())
                .build())
            .build();
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
    public List<ProductAssestResponseDTO> handleGetProductAssestByProduct(Long productId) {
        Product currentProduct = this.productRepository.findById(productId).orElseThrow(() -> 
            new NoSuchElementException("Product not found")
        );
        return this.productAssestRepository.findByProductAndDeletedAtIsNull(currentProduct).stream().map(product_assest -> {
            return ProductAssestResponseDTO.builder()
                .id(product_assest.getId())
                .assest_url(product_assest.getAssest_url())
                .is_main(product_assest.getIs_main())
                .product(ProductAssestProduct.builder()
                    .id(product_assest.getProduct().getId())
                    .build())
                .build();
        }).toList();
    }
    



}
