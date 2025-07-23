package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.enumType.StatusType;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.security.JwtGenerator;
import com.CafeSystem.cafe.security.UserPrincipal;
import com.CafeSystem.cafe.service.UserService;
import com.CafeSystem.cafe.service.email.EmailService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtGenerator jwtGenerator;
    private final CurrentUserUtil currentUserUtil;
    private final EmailService emailService;
    private final UserPrincipal userPrincipal;
    private final UserMapper userMapper;

    @Override
    public Page<UserProfileDto> AllUserAndAdmin(int page, int limit) {
        log.info("AllUserAndAdmin Function STARTED");

        if (userPrincipal.isAdmin()) {
            log.info("Admin accessed getAll endpoint");
            Pageable pageable = PageRequest.of(page, limit);
            Page<User> all = userRepository.findAll(pageable);
            log.debug("Total user and admin found: {}", all.getTotalElements());

            return all.map(userMapper::mapToUserProfileDto);

        } else {
            log.warn("Unauthorized access attempt to getAllUsersAndAdmin");
            throw new HandleException("Unauthorized: Only 'Admin' can access this");
        }
    }

    @Override
    public Page<UserProfileDto> getAllAdmin(int offset, int limit) {
        log.info("AllAdmin Function STARTED");
        if (currentUserUtil.isAdmin()) {
            log.info("Admin accessed getAllAdmin endpoint");
            Pageable pageable = PageRequest.of(offset, limit);
            Page<User> admins = userRepository.getAllAdmin(pageable);
            log.debug("Total admins found: {}", admins.getTotalElements());

            return admins.map(userMapper::mapToUserProfileDto);

        } else {
            log.warn("Unauthorized access attempt to getAllAdmin");
            throw new HandleException("Unauthorized: Only 'Admin' can access this");
        }
    }

    @Override
    public Page<UserProfileDto> getAllUsers(int offset, int size) {
        log.info("GetAllUsers Function STARTED");
        if(currentUserUtil.isAdmin()){
            Pageable pageable = PageRequest.of(offset, size);
            Page<User> users = userRepository.getAllUser(pageable);
            log.debug("Total users found: {}", users.getTotalElements());

            return users.map(userMapper::mapToUserProfileDto);
        }else{
            log.info("Unauthorized access attempt to getAllUsers");
            throw new HandleException("Unauthorized: Only 'Admin' can access this");
        }
    }

    @Override
    public ResponseEntity<ApiResponse<UpdateComparisonDto>> updateUser(UpdateUserRequest userRequest, int id) {
        log.info("Starting updateUser process for userId: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new HandleException("User By {" + id + "} id not found");
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                        .orElseThrow(()->new HandleException("User not found"));

        if (!currentUser.getId().equals(id)) {
            throw new HandleException("You are not authorized to update this account");
        }

        log.debug("Loaded existing user data for ID: {}", user.getId());

        UpdateResponse oldDataUser = UpdateResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .modifiedAt(user.getModifiedAt())
                .build();

        Optional.ofNullable(userRequest.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userRequest.getContactNumber()).ifPresent(user::setContactNumber);
        Optional.ofNullable(userRequest.getName()).ifPresent(user::setName);


        Optional<User> founded = userRepository.findByEmail(userRequest.getEmail());
        if(founded.isPresent() && !Objects.equals(founded.get().getId(), user.getId())){
            throw new HandleException("Sorry, Email already exits");
        }

        User updatedUser = userRepository.save(user);
        log.info("User with ID {} updated successfully", updatedUser.getId());

        UpdateResponse newDataUser = UpdateResponse.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .contactNumber(updatedUser.getContactNumber())
                .modifiedAt(updatedUser.getModifiedAt())
                .build();

        UpdateComparisonDto comparison = UpdateComparisonDto.builder()
                .before(oldDataUser)
                .after(newDataUser)
                .build();

        ApiResponse<UpdateComparisonDto> updated = ApiResponse.<UpdateComparisonDto>builder()
                .status("success")
                .message("Data updated successfully!")
                .data(comparison)
                .build();

        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> updateStatusByAdmin(StatusUpdateRequest request) {
        if (!userPrincipal.isAdmin()) {
            log.warn("Unauthorized access attempt to update user status detected.");
            return CafeUtil.getResponseEntity("You are not authorized to perform this action.", HttpStatus.BAD_REQUEST);
        }

        Integer userId = request.getId();
        String requestedStatus = request.getStatus();
        String token = request.getToken();

        log.info("Admin initiated status update for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Status update failed - User with ID {} not found", userId);
                    return new HandleException("User not found with ID: " + userId);
                });

        StatusType newStatus;
        try {
            newStatus = StatusType.valueOf(requestedStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status provided: {}", requestedStatus);
            throw new HandleException("Invalid status: " + requestedStatus);
        }

        userRepository.updateStatus(newStatus, userId);

        List<User> admins = userRepository.getAllAdmin();
        sendEmailToAllAdmin(newStatus.name(), user.getEmail(), admins, token, userId);

        log.info("User status successfully updated for ID: {}", userId);
        return ResponseEntity.ok("User status has been successfully updated.");
    }

    public void sendEmailToAllAdmin(String status, String userEmail, List<User> allAdmin, String token, int id) {
        String currentEmail = jwtGenerator.extractUsername(token);
        User user = userRepository.findById(id).orElseThrow(() -> new HandleException("User not found"));

        List<User> admins = allAdmin.stream()
                .filter(admin -> !admin.getEmail().equals(currentEmail))
                .collect(Collectors.toList());

        String adminSubject;
        String adminBody;

        String userSubject;
        String userBody;

        switch (status.toUpperCase()) {
            case "ACTIVE" -> {
                adminSubject = "Account Activation Notification";
                adminBody = "Dear Admins,\n\n" +
                        "The account with email: " + userEmail + " has been *activated* by administrator: " + currentEmail + ".\n\n" +
                        "Best regards,\nCafe Management System";

                userSubject = "Your Account Has Been Activated!";
                userBody = "Dear " + user.getName() + ",\n\n" +
                        "Good news! Your account has been *activated* and is now ready to use.\n\n" +
                        "Welcome to the system!\n\n" +
                        "Best regards,\nCafe Management System";
            }

            case "INACTIVE" -> {
                adminSubject = "Account Deactivation Notification";
                adminBody = "Dear Admins,\n\n" +
                        "The account with email: " + userEmail + " has been *deactivated* by administrator: " + currentEmail + ".\n\n" +
                        "Best regards,\nCafe Management System";

                userSubject = "Your Account Has Been Deactivated";
                userBody = "Dear " + user.getName() + ",\n\n" +
                        "We would like to inform you that your account has been *deactivated* by the system administrator.\n\n" +
                        "If you think this was done in error, please contact support.\n\n" +
                        "Best regards,\nCafe Management System";
            }

            case "PENDING" -> {
                adminSubject = "Account Status Set to Pending";
                adminBody = "Dear Admins,\n\n" +
                        "The account with email: " + userEmail + " has been moved to *PENDING* status by administrator: " + currentEmail + ".\n\n" +
                        "Best regards,\nCafe Management System";

                userSubject = "Your Account is Under Review";
                userBody = "Dear " + user.getName() + ",\n\n" +
                        "Your account status has been set to *PENDING*. This means it is currently under review.\n\n" +
                        "We'll notify you once a decision has been made.\n\n" +
                        "Best regards,\nCafe Management System";
            }

            case "BLOCKED" -> {
                adminSubject = "Account Blocked Notification";
                adminBody = "Dear Admins,\n\n" +
                        "The account with email: " + userEmail + " has been *BLOCKED* by administrator: " + currentEmail + ".\n\n" +
                        "Please review this action if necessary.\n\n" +
                        "Best regards,\nCafe Management System";

                userSubject = "Your Account Has Been Blocked";
                userBody = "Dear " + user.getName() + ",\n\n" +
                        "We regret to inform you that your account has been *BLOCKED* due to a violation of our policies or as per administrative decision.\n\n" +
                        "If you believe this is a mistake, please contact the administration.\n\n" +
                        "Best regards,\nCafe Management System";
            }

            default -> {
                log.warn("Unknown status received: {}", status);
                return;
            }
        }

        emailService.sendEmailToAdmins(adminSubject, adminBody, admins);
        emailService.sendEmailToAdmins(userSubject, userBody, List.of(user));
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtil.getResponseEntity(" Token is valid", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaginatedResponse<UserProfileDto>> searchByUsername(String name,  int offset,
                                                                              int size, HttpServletRequest request) {
        if (!userPrincipal.isAdmin()) {
            throw new HandleException("Unauthorized: Only admin users are allowed to perform this action");
        }
        Pageable pageable = PageRequest.of(offset, size);
        Page<User> result =  userRepository.searchByNameContainingIgnoreCase(name, pageable);
        Page<UserProfileDto> userResult = result.map(userMapper::mapToUserProfileDto);

        String extraParams = "&name=" + name;
        PaginatedResponse <UserProfileDto> mapResult = currentUserUtil.buildPaginatedResponseToSearchByName(
                userResult,
                offset,
                size,
                request.getRequestURL().toString()
                ,"offset",
                "size",
                extraParams
        );

        return ResponseEntity.ok(mapResult);
    }
}
