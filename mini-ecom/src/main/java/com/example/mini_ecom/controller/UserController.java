package com.example.mini_ecom.controller;

import java.time.Instant;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.mini_ecom.dto.user.GetUserResponseDTO;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.service.UserService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDTO<?>> getAllUsers(
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable userPageable
    ) {
        Page<User> currentPage = this.userService.handleGetAllUsers(userPageable);  
        List<GetUserResponseDTO> listUserResponseDTO = currentPage.getContent().stream().map(user -> {
            return GetUserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .gender(user.getGender())
            .build();
        }).toList();

        PaginationResponseDTO<GetUserResponseDTO, MetaDTO> paginationResponseDTO = PaginationResponseDTO.<GetUserResponseDTO, MetaDTO>builder()
        .result(listUserResponseDTO)
        .meta(MetaDTO.builder()
            .page(currentPage.getNumber())
            .pageSize(currentPage.getSize())
            .pages(currentPage.getTotalPages())
            .total(currentPage.getTotalElements())
            .build()
        )
        .build();

        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get all users successfully")
            .build()
        )
        .data(paginationResponseDTO)
        .timeStamp(Instant.now())
        .build());
    }

    // sẽ thay bằng register
    @PostMapping("/users")
    public ResponseEntity<ApiResponseDTO<?>> createUser(
        @Valid @RequestBody User newUser
    ) {
        User createdUser = this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.CREATED)
            .message("Create user successfully")
            .build()
        )
        .data(createdUser)
        .timeStamp(Instant.now())
        .build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getUserById(
        @PathVariable Long id
    ) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Get user successfully")
            .build()
        )
        .data(user)
        .timeStamp(Instant.now())
        .build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateUser(
        @PathVariable Long id,
        @RequestBody User updateUser
    ) {
        User updatedUser = this.userService.handleUpdateUser(id, updateUser);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Update user successfully")
            .build()
        )
        .data(updatedUser)
        .timeStamp(Instant.now())
        .build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteUser(
        @PathVariable Long id
    ) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(ApiResponseDTO.builder()
        .status(ApiResponseDTO.ResponseStatusDTO.builder()
            .statusCode(HttpStatus.OK)
            .message("Delete user successfully")
            .build()
        )
        .timeStamp(Instant.now())
        .build());
        
    }
}
