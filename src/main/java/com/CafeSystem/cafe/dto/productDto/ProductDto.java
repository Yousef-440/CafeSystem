package com.CafeSystem.cafe.dto.productDto;

import com.CafeSystem.cafe.model.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
    private String name;
    private int categoryId;
    @NotNull(message = "The price must be not null")
    private String description;
    private Integer quantity;

    @NotNull(message = "The price must be not null")
    @DecimalMin(value = "1.5", message = "minimum price => '1.5'")
    private Double price;

}
