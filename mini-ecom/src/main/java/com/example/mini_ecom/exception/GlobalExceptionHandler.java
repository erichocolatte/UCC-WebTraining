package com.example.mini_ecom.exception;

import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.mini_ecom.dto.ApiResponseDTO;


// @RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.builder()
                        .status(ApiResponseDTO.ResponseStatusDTO.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(e.getMessage())
                            .build()
                        )
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .status(ApiResponseDTO.ResponseStatusDTO.builder()
                            .statusCode(HttpStatus.BAD_REQUEST)
                            .message(e.getMessage())
                            .build()
                        )
                        .build());
    }
    

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.builder()
                        .status(ApiResponseDTO.ResponseStatusDTO.builder()
                            .statusCode(HttpStatus.NOT_FOUND)
                            .message(e.getMessage())
                            .build()
                        )
                        .build());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .status(ApiResponseDTO.ResponseStatusDTO.builder()
                            .statusCode(HttpStatus.BAD_REQUEST)
                            .message(e.getMessage())
                            .build()
                        )
                        .build());
    }
}
