package com.CafeSystem.cafe.dto.bill;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailsDTO {
    private String name;
    private String category;
    private int quantity;
    private Double price;
    private Double subTotal;
}
