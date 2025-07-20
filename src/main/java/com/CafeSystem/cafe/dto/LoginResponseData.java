package com.CafeSystem.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseData {
    private String message;
    private String email;
    private String token;
    private String refreshToken;
}
