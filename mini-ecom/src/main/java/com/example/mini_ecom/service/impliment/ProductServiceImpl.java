package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.dto.product.ProductAssestDTO;
import com.example.mini_ecom.dto.product.ProductCategorieDTO;
import com.example.mini_ecom.dto.product.ProductResponseDTO;
import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.model.Product;
import com.example.mini_ecom.model.ProductAssest;
import com.example.mini_ecom.repository.CartItemRepository;
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
    private final CartItemRepository cartItemRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategorieRepository categorieRepository, ProductAssestRepository productAssestRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.categorieRepository = categorieRepository;
        this.productAssestRepository = productAssestRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    public ProductResponseDTO handleCreateProduct(Product newProduct) {
        if (newProduct.getCategorie().getId() == null) {
            throw new IllegalArgumentException("Categorie id is null");
        }

        if (!this.categorieRepository.findById(newProduct.getCategorie().getId()).isPresent()) {
            throw new NoSuchElementException("Categorie not found");
        }

        Product createdProduct = this.productRepository.save(newProduct);

        return ProductResponseDTO.builder()
            .id(createdProduct.getId())
            .name(createdProduct.getName())
            .description(createdProduct.getDescription())
            .price(createdProduct.getPrice())
            .stock(createdProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                .id(createdProduct.getCategorie().getId())
                .name(createdProduct.getCategorie().getName())
                .build())
            .assets(null)
            .build();
    }

    @Override
    public ProductResponseDTO handleGetProductById(Long id) {
        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        return ProductResponseDTO.builder()
            .id(currentProduct.getId())
            .name(currentProduct.getName())
            .description(currentProduct.getDescription())
            .price(currentProduct.getPrice())
            .stock(currentProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                .name(currentProduct.getCategorie().getName())
                .id(currentProduct.getCategorie().getId())
                .build())
            .assets(currentProduct.getProductAssets() != null ? currentProduct.getProductAssets().stream().map(asset -> ProductAssestDTO.builder()
                .id(asset.getId())
                .assest_url(asset.getAssest_url())
                .is_main(asset.getIs_main())
                .build()).toList() : null)
            .build();
    }

    @Override
    public ProductResponseDTO handleUpdateProduct(Long id, Product updateProduct) {
        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("Product not found"));

        if (updateProduct.getCategorie() != null) {
            if (updateProduct.getCategorie().getId() != null) {
                this.categorieRepository.findById(updateProduct.getCategorie().getId()).orElseThrow(() -> 
                new NoSuchElementException("Categorie not found"));

                currentProduct.getCategorie().setId(updateProduct.getCategorie().getId());
            }
        }

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

        Product updatedProduct = this.productRepository.save(currentProduct);

        return ProductResponseDTO.builder()
            .id(updatedProduct.getId())
            .name(updatedProduct.getName())
            .description(updatedProduct.getDescription())
            .price(updatedProduct.getPrice())
            .stock(updatedProduct.getStock())
            .categorie(ProductCategorieDTO.builder()
                .name(updatedProduct.getCategorie().getName())
                .id(updateProduct.getCategorie().getId())
                .build())
            .build();
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

        this.cartItemRepository.findByProductAndDeletedAtIsNull(currentProduct).stream().map(cartItem -> {
            cartItem.setDeletedAt(Instant.now());
            return cartItem;
        }).toList();

        // this.productAssestRepository.saveAll(listProductAsset);

        // this.productRepository.delete(currentProduct);  
        currentProduct.setDeletedAt(Instant.now());
        this.productRepository.save(currentProduct);
    }

    @Cacheable(
    value = "allProducts",
    key = "#productPageable.pageNumber + '-' + #productPageable.pageSize + '-' + #productPageable.sort.toString()"
    )
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PaginationResponseDTO<ProductResponseDTO, MetaDTO> handleGetAllProducts(Pageable productPageable) {

        Page<Product> currentPage = productRepository.findAllByDeletedAtIsNull(productPageable);

        List<ProductResponseDTO> list = currentPage.getContent()
        .stream()
        .map(product -> ProductResponseDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .build()
        )
        .toList();


        return PaginationResponseDTO.<ProductResponseDTO, MetaDTO>builder()
                .result(list)
                .meta(MetaDTO.builder()
                        .page(currentPage.getNumber())
                        .pageSize(currentPage.getSize())
                        .pages(currentPage.getTotalPages())
                        .total(currentPage.getTotalElements())
                        .build())
                .build();
    }


    // @Override
    // public void handleDeleteProductCategorie(Categorie currentCategorie) {
    //     this.productRepository.deleteByCategorie(currentCategorie);
    // }

    @Override
    @Cacheable(
        value = "allProductsByCategory",
        key = "#categorieId + '-' + #productPageable.pageNumber + '-' + #productPageable.pageSize + '-' + #productPageable.sort.toString()"
    )
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PaginationResponseDTO<ProductResponseDTO, MetaDTO> handleGetAllProductsByCategory(Long categorieId, Pageable productPageable) {
        if (categorieId == null) {
            throw new IllegalArgumentException("Categorie id is null");
        }

        Categorie currentCategorie = this.categorieRepository.findById(categorieId).orElseThrow(() -> 
            new NoSuchElementException("Categorie not found")
        );

        Page<Product> currentPage = this.productRepository.findByCategorieAndDeletedAtIsNull(currentCategorie, productPageable);

        List<ProductResponseDTO> listProductResponseDTO = currentPage.getContent()
        .stream()
        .map(product -> ProductResponseDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .stock(product.getStock())
            .categorie(ProductCategorieDTO.builder()
                .name(product.getCategorie().getName())
                .id(product.getCategorie().getId())
                .build())
            .assets(product.getProductAssets() != null ? product.getProductAssets().stream().map(asset -> ProductAssestDTO.builder()
                .id(asset.getId())
                .assest_url(asset.getAssest_url())
                .is_main(asset.getIs_main())
                .build()).toList() : null)
            .build()
        )
        .toList();


        return PaginationResponseDTO.<ProductResponseDTO, MetaDTO>builder()
                .result(listProductResponseDTO)
                .meta(MetaDTO.builder()
                        .page(currentPage.getNumber())
                        .pageSize(currentPage.getSize())
                        .pages(currentPage.getTotalPages())
                        .total(currentPage.getTotalElements())
                        .build())
                .build();
    }
}
