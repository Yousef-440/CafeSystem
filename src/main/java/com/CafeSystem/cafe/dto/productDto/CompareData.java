package com.CafeSystem.cafe.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompareData {
    private ProductUpdateResponse oldData;
    private ProductUpdateResponse newData;
}
