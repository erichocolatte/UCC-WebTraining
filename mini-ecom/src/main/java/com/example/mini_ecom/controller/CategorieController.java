package com.example.mini_ecom.controller;

import java.time.Instant;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.Meta;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mini_ecom.dto.ApiResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO;
import com.example.mini_ecom.dto.PaginationResponseDTO.MetaDTO;
import com.example.mini_ecom.model.Categorie;
import com.example.mini_ecom.service.CategorieService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1")
public class CategorieController {
    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping("/categories") 
    public ResponseEntity<ApiResponseDTO<?>> getAllCategories(
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable categoriePageable
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all categories successfully")
            .build())
        .data(this.categorieService.handleGetAllCategories(categoriePageable))
        .timeStamp(Instant.now())
        .build());

    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponseDTO<?>> createCategorie(
        @Valid @RequestBody Categorie newCategorie
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create categorie successfully")
            .build())
        .data(this.categorieService.handleCreateCategorie(newCategorie))
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getCategorieById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get categorie successfully")
            .build())
        .data(this.categorieService.handleGetCategorieById(id))
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateCategorie(
        @PathVariable Long id,
        @RequestBody Categorie updateCategorie
    ) {
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update categorie successfully")
            .build())
        .data(this.categorieService.handleUpdateCategorie(id, updateCategorie))
        .timeStamp(Instant.now())
        .build());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteCategorie(
        @PathVariable Long id
    ) {
        this.categorieService.handleDeleteCategorie(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Delete categorie successfully")
            .build())
        .timeStamp(Instant.now())
        .build());
    }


}
