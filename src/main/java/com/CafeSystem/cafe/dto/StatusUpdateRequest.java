package com.CafeSystem.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusUpdateRequest {
    private String status;
    private Integer id;
    private String token;
}
