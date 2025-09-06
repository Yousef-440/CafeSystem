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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpUserDto {

//    Name User field

    @Schema(
            description = "The user's full name. It should be between 3 and 25 characters long " +
                    "and contain only letters (no numbers or special characters).",
            example = "Yousef"
    )
    @NotBlank(message = "Please enter your name")
    @Size(min = 3, max = 25, message = "Name must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name can only contain letters")
    private String name;

//    Name User field


//    Email User Field

    @Schema(
            description = "The user's email address. Must be a valid email format like 'example@domain.com'.",
            example = "example@domain.com"
    )
    @NotBlank(message = "Please enter your email address")
    @Email(message = "Please enter a valid email address (e.g., example@domain.com)")
    private String email;

//    Email User Field


//    Phone-Number User Field

    @Schema(
            description = "The user's contact number. " +
                    "Must be a valid Jordanian phone number with 10 digits (e.g., 0791234567).",
            example = "0791234567"
    )
    @NotBlank(message = "Please enter your contact number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be exactly 10 digits (e.g., 0791234567)")
    private String contactNumber;

//    Phone-Number User Field


//    Pass User Field

    @Schema(
            description = "A strong password for the user account. " +
                    "It must be 6-20 characters long, include at least one digit, " +
                    "one lowercase letter, one uppercase letter, and one special character (e.g., @, #, $, !).",
            example = "Pass@123"
    )
    @NotBlank(message = "Please enter your password")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must include at least one digit, one lowercase letter, one uppercase letter, and one special character (e.g., @, #, $, !)"
    )
    private String password;

//    Pass User Field
}
