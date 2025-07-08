package com.CafeSystem.cafe.dto.productDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductUpdateResponse {
    private int id;
    private String name;
    private String description;
}
