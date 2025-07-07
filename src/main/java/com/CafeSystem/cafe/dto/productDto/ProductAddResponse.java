package com.CafeSystem.cafe.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductAddResponse {
    private String nameProduct;
    private String nameCategory;
    private LocalDate createdAt;
}
