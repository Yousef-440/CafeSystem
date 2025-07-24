package com.CafeSystem.cafe.dto.categoryDto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 3, max = 25, message = "Name must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "The name must be letters only")
    @Schema(name = "pizza")
    private String name;
}
