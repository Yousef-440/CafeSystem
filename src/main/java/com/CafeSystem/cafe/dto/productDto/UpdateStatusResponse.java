package com.CafeSystem.cafe.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateStatusResponse {
    private int id;
    private String nameProduct;
    private Double price;
    private String status;
    private int categoryId;
    private String categoryName;
}
