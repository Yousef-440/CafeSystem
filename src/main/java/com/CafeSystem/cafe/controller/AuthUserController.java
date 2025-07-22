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

@Slf4j
@Tag(name = "Authentication User Controller", description = "Create new account and login," +
        " forget your password and change your password")
@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthUserService userService;

    @Operation(
            summary = "Create new account",
            description = "To create a new account, the user must enter the name," +
                    " email, phone-number and password."
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpUserResponse>> signup(@Valid @RequestBody SignUpUserDto signUpUserDto) throws MessagingException {
        return userService.signup(signUpUserDto);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        return userService.verifyEmail(token);
    }

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
