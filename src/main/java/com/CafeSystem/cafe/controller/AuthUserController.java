package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(
        name = "Authentication User Controller",
        description = "APIs for user authentication and account management:" +
                " signup, login, password reset, and password change."
)


@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class AuthUserController {
    private final AuthUserService userService;

    // ===================== SignUp =====================

    @Operation(
            summary = "Register a new user account",
            description = "This endpoint allows users to create a new account by providing their full name, " +
                    "email address, phone number, and a strong password. " +
                    "All fields are required. The password must meet security standards, " +
                    "and the email must be unique. Proper validation messages will be returned for any errors."
    )

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpUserResponse>> signup(@Valid @RequestBody SignUpUserDto signUpUserDto) throws MessagingException {
        return userService.signup(signUpUserDto);
    }

    // ===================== SignUp =====================

    // ===================== Verification Email =====================

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        return userService.verifyEmail(token);
    }

    // ===================== Verification Email =====================


    @Operation(
            summary = "Login",
            description = "To Login your account, the user must enter the email and password."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseData>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @Operation(
            summary = "Refresh Token",
            description = "Generates a new access token using a valid refresh token to maintain user authentication without requiring re-login."
    )
    @PostMapping(path = "/refresh")
    public ResponseEntity<?> refreshToken (@RequestBody RefreshRequest refreshToken){
        return userService.refresh(refreshToken.getRefreshToken());
    }

    @Operation(
            summary = "change the password",
            description = "Change password using old password verification"
    )
    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest){
        return userService.changePassword(passwordChangeRequest);
    }

    @Operation(
            summary = "forgot password",
            description = "Forgetting the password is done by verifying the email and sending" +
                    " a token to this email and placing this token in restPassword endpoint."
    )
    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest emailRequest) throws MessagingException, IOException {
        return userService.forgotPassword(emailRequest.getEmail());
    }

    @Operation(
            summary = "rest password",
            description = "The password is changed," +
                    " but you must ensure the token is used if you forget the password."
    )
    @PostMapping(path = "/resetPassword")
    public ResponseEntity<String> restPassword(
            @RequestParam String passwordRestToken,
            @Valid @RequestBody PasswordResetRequest passwordResetRequest){
        log.info("restPassword endpoint is started");
        return userService.resetPassword(passwordRestToken, passwordResetRequest.getNewPassword());
    }
}
