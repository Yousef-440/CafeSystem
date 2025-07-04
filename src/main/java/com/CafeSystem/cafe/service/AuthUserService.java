package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.*;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface AuthUserService {
    ResponseEntity <ApiResponse<?>> signup(UserDto userDto) throws MessagingException;

    ResponseEntity<ApiResponse<LoginResponseData>> login(LoginRequest loginRequest);

    ResponseEntity<String> changePassword(PasswordChangeRequest passwordChangeRequest);

    ResponseEntity<String> forgotPassword(String email) throws MessagingException;
}
