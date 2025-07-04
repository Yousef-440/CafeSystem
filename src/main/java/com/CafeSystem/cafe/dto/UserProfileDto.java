package com.CafeSystem.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Integer id;
    private String name;
    private String email;
    private String contactNumber;
    private String status;
    private String role;
}
