package com.CafeSystem.cafe.dto.categoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateCategoryResponseDto {
    private UpdateDataResponse oldData;
    private UpdateDataResponse newData;
}
