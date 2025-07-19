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

    @Schema(
            description = "Full name of the user. Must be between 3 and 25 characters and" +
                    " not contain any special character",
            example = "Yousef"
    )
    @NotBlank(message = "Please fill in the name")
    @Size(min = 3, max = 25, message = "Name must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "The name must be letters only")
    private String name;

    @Schema(
            description = "Valid email address of the user.",
            example = "someone...@example.com"
    )
    @NotBlank(message = "Please fill in the email")
    @Email(message = "Email format is invalid")
    private String email;

    @Schema(
            description = "Must be Jordanian number",
            example = "079*******"
    )
    @NotBlank(message = "Please fill in the contact-number ")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must contain 10 digits")
    private String contactNumber;

    @Schema(
            description = "Secure password. Must include at least one digit," +
                    " one lowercase letter, one uppercase letter," +
                    " and one special character. Length between 6 and 20 characters.",
            example = "Pass@123"
    )
    @NotBlank(message = "Please fill in the password")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"
    )
    private String password;
}
