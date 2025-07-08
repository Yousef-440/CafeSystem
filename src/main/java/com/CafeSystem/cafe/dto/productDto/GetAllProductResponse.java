package com.CafeSystem.cafe.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetAllProductResponse {
    private int id;
    private String name;
    private String description;
    private double price;
    private String status;
    private int categoryId;
    private String categoryName;
}
