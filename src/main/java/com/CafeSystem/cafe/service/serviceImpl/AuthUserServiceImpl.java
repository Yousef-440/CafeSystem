package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.enumType.RoleType;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.PasswordResetToken;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.PasswordResetTokenRepository;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.security.CustomUserDetails;
import com.CafeSystem.cafe.security.JwtGenerator;
import com.CafeSystem.cafe.service.AuthUserService;
import com.CafeSystem.cafe.service.email.EmailService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private CurrentUserUtil currentUserUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> signup(UserDto userDto) throws MessagingException {
        log.info("Starting signup process for email: {}", userDto.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Signup failed: Email already exists - {}", userDto.getEmail());
            ApiResponse<?> errorResponse = ApiResponse.<Void>builder()
                    .status("error")
                    .message("Sorry, Email already exists")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        String subject = "Hello, " + userDto.getName();
        emailService.sendWhenSignup(userDto.getEmail(),subject, userDto.getName());

        User user = convertDtoToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(RoleType.USER);
        user.setStatus("false");

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        UserResponse userResponse = UserResponse.builder()
                .message("Hello, Welcome to the Cafe, " + savedUser.getName() + " !")
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .contactNumber(savedUser.getContactNumber())
                .createdAt(savedUser.getCreatedAt())
                .build();

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("Account created successfully!")
                .data(userResponse)
                .build();

        log.info("Signup successfully for email: {}", savedUser.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponseData>> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        log.info("Login attempt for email: {}", email);

        Authentication authentication;
        try {

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            log.info("Authentication successful for email: {}", email);

        } catch (BadCredentialsException e) {

            log.warn("Wrong password for email: {}", email);
            throw new HandleException("Incorrect Password");

        } catch (Exception e) {

            log.error("Authentication failed for email: {}", email);
            throw e;

        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        String token = jwtGenerator.generateToken(userDetails, userDetails.getRole().name());
        log.info("information of token userDetails: {} role: {}", userDetails, userDetails.getRole().name());

        String welcomeMessage = "Welcome Back, " + userDetails.getName().toUpperCase();

        LoginResponseData loginData = LoginResponseData.builder()
                .message(welcomeMessage)
                .email(userDetails.getEmail())
                .token(token)
                .build();

        ApiResponse<LoginResponseData> response = ApiResponse.<LoginResponseData>builder()
                .status("success")
                .message("Login successful!")
                .data(loginData)
                .build();

        log.info("Login response prepared for user: {}", userDetails.getEmail());
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<String> changePassword(PasswordChangeRequest passwordChangeRequest) {
        log.info("changePassword Function Started");
        try {
            User user = currentUserUtil.getCurrentUser()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
                log.info(" Password updated successfully for user: {}", user.getEmail());

                if (passwordChangeRequest.getOldPassword().equals(passwordChangeRequest.getNewPassword())) {
                    log.warn("New password matches the old password for user: {}", user.getEmail());
                    return CafeUtil.getResponseEntity("New password must be different from old password.",
                            HttpStatus.BAD_REQUEST);
                }

                user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
                userRepository.save(user);
                emailService.sendWhenChangePassword(user.getEmail());
                return CafeUtil.getResponseEntity("Password Updated Successfully", HttpStatus.OK);

            } else {

                log.info("Incorrect old password attempt for user: {}",user.getEmail());
                return CafeUtil.getResponseEntity("Incorrect old password. Please try again",
                        HttpStatus.BAD_REQUEST);

            }
        }catch (Exception ex){
            log.error("Error occurred while changing password for user: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity("Something Went Wrong", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> forgotPassword(String email) throws MessagingException, IOException {

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new HandleException("Sorry, Email Not Found"));
        
        if (user.getEmail() != null) {
            emailService.sendResetLink(
                    user.getEmail(),
                    passwordResetService.createRestToken(user)
            );
            log.info("Password reset link sent to: {}", user.getEmail());
            return CafeUtil.getResponseEntity("Check Your Email", HttpStatus.OK);
        }

        return CafeUtil.getResponseEntity("User Email is Invalid", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> resetPassword(String passwordRestToken, String newPass) {
        PasswordResetToken token =
                passwordResetTokenRepository.findByToken(passwordRestToken).orElseThrow(
                        ()->new HandleException("Invalid Token")
                );

        if (token.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);

        return ResponseEntity.ok("Password successfully reset");
    }

    private User convertDtoToEntity(UserDto userDto) {
        log.debug("Converting UserDto to User entity : {}", userDto.getName());
        return userMapper.toEntity(userDto);
    }
}
