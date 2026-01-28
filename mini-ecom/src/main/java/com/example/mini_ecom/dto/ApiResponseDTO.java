package com.example.mini_ecom.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Getter
// @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiResponseDTO<T> {
    private ResponseStatusDTO status;
    private T data;
    private Instant timeStamp;

    // @Getter
    // @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ResponseStatusDTO {
        private HttpStatus statusCode;
        private String message;
    }

}
