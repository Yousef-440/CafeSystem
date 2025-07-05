package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AuthUserService {
    ResponseEntity <ApiResponse<?>> signup(UserDto userDto) throws MessagingException;

    ResponseEntity<ApiResponse<LoginResponseData>> login(LoginRequest loginRequest);

    ResponseEntity<String> changePassword(PasswordChangeRequest passwordChangeRequest);

    ResponseEntity<String> forgotPassword(String email) throws MessagingException, IOException;

    ResponseEntity<String> resetPassword(String passwordRestToken, String newPass);
}
