package com.CafeSystem.cafe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateComparisonDto {
    private UpdateResponse before;
    private UpdateResponse after;
}
