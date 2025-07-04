package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.model.PasswordResetToken;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.PasswordResetTokenRepository;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.service.AuthUserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
public class AuthUserController {
    private final AuthUserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) throws MessagingException {
        return userService.signup(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseData>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }


    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
        return userService.changePassword(passwordChangeRequest);
    }

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest emailRequest) throws MessagingException {
        return userService.forgotPassword(emailRequest.getEmail());
    }

    @PostMapping(path = "/restPassword")
    public ResponseEntity<String> restPassword(@RequestBody PasswordResetRequest passwordResetRequest){
       PasswordResetToken token =
               passwordResetTokenRepository.findByToken(passwordResetRequest.getToken()).orElseThrow(
                       ()->new HandleException("Invalid Token")
               );

        if (token.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);

        return ResponseEntity.ok("Password successfully reset");
    }
}
