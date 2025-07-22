package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.security.JwtAuthFilter;
import com.CafeSystem.cafe.security.JwtGenerator;
import com.CafeSystem.cafe.security.UserPrincipal;
import com.CafeSystem.cafe.service.UserService;
import com.CafeSystem.cafe.service.email.EmailService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private CurrentUserUtil currentUserUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserPrincipal userPrincipal;
    @Autowired
    private UserMapper userMapper;

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

            return admins.map(this::mapToUserProfileDto);

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

            return users.map(this::mapToUserProfileDto);
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
        if (currentUserUtil.isAdmin()) {
            log.info("Admin initiated status update for user with ID: {}", request.getId());

            User user = userRepository.findById(request.getId())
                    .orElseThrow(() -> {
                        log.error("Status update failed - User with ID {} not found", request.getId());
                        return new HandleException("User not found with ID: " + request.getId());
                    });

            sendEmailToAllAdmin(request.getStatus(), user.getEmail(), userRepository.getAllAdmin(), request.getToken());

            userRepository.updateStatus(request.getStatus(), request.getId());

            log.info("User status successfully updated for ID: {}", request.getId());
            return new ResponseEntity<>("User status has been successfully updated.", HttpStatus.OK);
        }

        log.warn("Unauthorized access attempt to update user status detected.");
        return CafeUtil.getResponseEntity("You are not authorized to perform this action.", HttpStatus.BAD_REQUEST);
    }


    public void sendEmailToAllAdmin(String status, String userEmail, List<User> allAdmin, String token) {
        String currentEmail = jwtGenerator.extractUsername(token);

        List<User> ccAdmins = allAdmin.stream()
                .filter(admin -> !admin.getEmail().equals(currentEmail))
                .toList();

        String subject;
        String body;

        if (status.equalsIgnoreCase("true")) {
            subject = "Account Approval Notification";
            body = "Dear Admins,\n\n" +
                    "This is to inform you that the account associated with the email: " + userEmail +
                    " has been 'approved' by administrator: " + currentEmail + ".\n\n" +
                    "If you have any concerns, please contact the approving administrator directly.\n\n" +
                    "Best regards,\nCafe Management System";
        } else {
            subject = "Account Deactivation Notification";
            body = "Dear Admins,\n\n" +
                    "This is to notify you that the account associated with the email: " + userEmail +
                    " has been 'deactivated' by administrator: " + currentEmail + ".\n\n" +
                    "For further details or questions, please follow up with the responsible admin.\n\n" +
                    "Best regards,\nCafe Management System";
        }

        log.info("Dispatching email notification: [{}] to admin: {}, CC {} admins", subject, currentEmail, ccAdmins.size());

        emailService.sendEmailToAdmins(
                subject,
                body,
                ccAdmins
        );
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

    private UserProfileDto mapToUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .status(user.getStatus().name())
                .role(user.getRole().name())
                .build();
    }
}
