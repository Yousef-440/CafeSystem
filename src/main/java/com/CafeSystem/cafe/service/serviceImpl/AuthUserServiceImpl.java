package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.enumType.RoleType;
import com.CafeSystem.cafe.enumType.StatusType;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.PasswordResetToken;
import com.CafeSystem.cafe.model.RefreshToken;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.model.UserVerificationToken;
import com.CafeSystem.cafe.repository.PasswordResetTokenRepository;
import com.CafeSystem.cafe.repository.RefreshTokenRepository;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.repository.UserVerificationTokenRepository;
import com.CafeSystem.cafe.security.CustomUserDetails;
import com.CafeSystem.cafe.security.JwtGenerator;
import com.CafeSystem.cafe.service.AuthUserService;
import com.CafeSystem.cafe.service.email.EmailService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final CurrentUserUtil currentUserUtil;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserVerificationTokenRepository userVerificationTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<SignUpUserResponse>> signup(SignUpUserDto signUpUserDto) throws MessagingException {
        log.info("Starting signup process for email: {}", signUpUserDto.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(signUpUserDto.getEmail());

        if (existingUser.isPresent()) {
            log.warn("Signup failed: Email already exists - {}", signUpUserDto.getEmail());
            throw new HandleException("Sorry, Email already exists");
        }

        User user = userMapper.toEntity(signUpUserDto);
        user.setPassword(passwordEncoder.encode(signUpUserDto.getPassword()));
        user.setRole(RoleType.USER);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        String token = UUID.randomUUID().toString();
        UserVerificationToken userVerificationToken = UserVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        userVerificationTokenRepository.save(userVerificationToken);

        String verifyUrl = "http://localhost:8081/api/v1/user/verify?token=" + token;
        String subject = "Hello, " + signUpUserDto.getName();
        emailService.sendWhenSignup(signUpUserDto.getEmail(),subject, signUpUserDto.getName(), verifyUrl);


        SignUpUserResponse signUpUserResponse = SignUpUserResponse.builder()
                .message("Hello,Welcome to the Cafe, " + savedUser.getName() + "!")
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .contactNumber(savedUser.getContactNumber())
                .createdAt(savedUser.getCreatedAt().toLocalDate())
                .build();

        ApiResponse<SignUpUserResponse> response = ApiResponse.<SignUpUserResponse>builder()
                .status("success")
                .message("Account created successfully!")
                .data(signUpUserResponse)
                .build();

        log.info("Signup successfully for email: {}", savedUser.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> verifyEmail(String token) {
        log.info("Verification is started");

       UserVerificationToken vToken = userVerificationTokenRepository.findByToken(token)
               .orElseThrow(()->new HandleException("Invalid Token"));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Token in verification is Token Expired");
            return ResponseEntity.badRequest().body("Token Expired");
        }

        User user = vToken.getUser();
        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);
        log.info("Account is ACTIVE");
        return ResponseEntity.ok("Email Verified Successfully");
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponseData>> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim();
        String password = loginRequest.getPassword().trim();

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

        String refresh = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();

        String welcomeMessage = "Welcome Back, " + userDetails.getName().toUpperCase();

        LoginResponseData loginData = LoginResponseData.builder()
                .message(welcomeMessage)
                .email(userDetails.getEmail())
                .token(token)
                .refreshToken(refresh)
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
    public ResponseEntity<?> refresh(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenService.getByToken(refreshToken);
        if(token.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        RefreshToken refresh = token.get();
        if (refreshTokenService.isExpired(refresh)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(refresh.getUser().getEmail());
        String newAccessToken = jwtGenerator.refreshToken(userDetails);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
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
}
