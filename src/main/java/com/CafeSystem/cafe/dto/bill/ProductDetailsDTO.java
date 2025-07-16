package com.CafeSystem.cafe.dto.bill;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailsDTO {
    private String name;
    private String category;
    private int quantity;

    @Positive
    @Digits(integer = 4, fraction = 2)
    private Double price;

    @Positive
    @Digits(integer = 4, fraction = 2)
    private Double subTotal;
}
