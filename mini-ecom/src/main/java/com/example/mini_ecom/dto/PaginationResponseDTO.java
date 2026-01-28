package com.example.mini_ecom.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

// @Getter
// @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaginationResponseDTO<T,M> {
    private List<T> result;
    private M meta;

    // @Getter
    // @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class MetaDTO {
        private int page;
        private int pageSize;
        private int pages;
        private Long total;
    }
}
