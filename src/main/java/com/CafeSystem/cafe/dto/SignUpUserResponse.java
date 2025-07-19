package com.CafeSystem.cafe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpUserResponse {
    private String message;
    private String name;
    private String email;
    private String contactNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
