package com.CafeSystem.cafe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateResponse {
    private String name;
    private String email;
    private String contactNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedAt;
}
