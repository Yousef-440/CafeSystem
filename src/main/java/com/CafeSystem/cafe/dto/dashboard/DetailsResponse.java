package com.CafeSystem.cafe.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DetailsResponse {
    private int numOfProduct;
    private int numOfBill;
    private int numOfCategory;
}
