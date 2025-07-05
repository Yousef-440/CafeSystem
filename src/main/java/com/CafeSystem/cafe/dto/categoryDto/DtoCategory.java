package com.CafeSystem.cafe.dto.categoryDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DtoCategory {
    @NotBlank(message = "Please fill in the name field")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
}
