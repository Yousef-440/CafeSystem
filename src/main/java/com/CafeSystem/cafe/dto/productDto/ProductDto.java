package com.CafeSystem.cafe.dto.productDto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto {

    @NotNull(message = "The name must be not null")
    @Size(min = 3, max = 35, message = "The name must be between 3 and 35 characters long")
    private String name;

    private int categoryId;

    @NotNull(message = "The price must be not null")
    private String description;

    @NotNull(message = "please enter number of quantity to product")
    private Integer quantity;

    @NotNull(message = "The price must be not null")
    @DecimalMin(value = "1.5", message = "minimum price => '1.5'")
    private Double price;

}
