package com.CafeSystem.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}