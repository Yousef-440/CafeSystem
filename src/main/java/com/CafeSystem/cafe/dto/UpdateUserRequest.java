package com.CafeSystem.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
public class UpdateUserRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 25, message = "Name must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "The name must be letters only")
    @Schema(description = "name of User" ,example = "Yousef")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "email of user", example = "someone...@example.com")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must contain 10 digits")
    @Schema(description = "Phone Number" , example = "079.......")
    private String contactNumber;
}
