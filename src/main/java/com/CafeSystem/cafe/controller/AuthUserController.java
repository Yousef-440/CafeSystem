package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
            summary = "change the password",
            description = "Change password using old password verification"
    )
    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
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
            @RequestBody PasswordResetRequest passwordResetRequest){
        return userService.resetPassword(passwordRestToken, passwordResetRequest.getNewPassword());
    }
}
