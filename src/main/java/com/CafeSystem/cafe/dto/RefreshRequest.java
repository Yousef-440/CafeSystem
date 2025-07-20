package com.CafeSystem.cafe.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshRequest {
    private String refreshToken;
}