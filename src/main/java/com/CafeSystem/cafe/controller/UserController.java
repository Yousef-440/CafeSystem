package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.service.UserService;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Controller", description = "Fetch and update data")
@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Operation(
            summary = "Get All User and Admin",
            description = "Retrieve user and admin in DB"
    )
    @GetMapping(path = "/getAll")
    public ResponseEntity<PaginatedResponse<UserProfileDto>> getAll(
            @Parameter(
                    description = "Page Number Starting from 1",
                    example = "1",
                    required = false
            ) @RequestParam(defaultValue = "1") int page,

            @Parameter(
                    description = "Number of users per page",
                    example = "3",
                    required = false
            ) @RequestParam(defaultValue = "3") int limitation,

            HttpServletRequest servletRequest
    ){
        int zeroBasedPage = page - 1;
        Page<UserProfileDto> allPerson = userService.AllUserAndAdmin(zeroBasedPage, limitation);

        PaginatedResponse<UserProfileDto> all = currentUserUtil.buildPaginatedResponse(
                allPerson,
                page,
                limitation,
                servletRequest.getRequestURL().toString(),
                "page","limitation"
        );

        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @Operation(
            summary = "Get All Admin",
            description = "Bring all users who have the admin role " +
                    "and no permission for the account that has the user role"
    )
    @Parameters({
            @Parameter(name = "offset", description = "Number of Page", example = "1"),
            @Parameter(name = "limit", description = "Number od element", example = "10")
    })
    @GetMapping(path = "/getAllAdmin")
    public ResponseEntity<PaginatedResponse<UserProfileDto>> getAllAdmin(
            @RequestParam(defaultValue = "1") int offset,
            @RequestParam(defaultValue = "2") int limit,
            HttpServletRequest request

    ){
       Page<UserProfileDto> allAdmins = userService.getAllAdmin(offset - 1, limit);

        PaginatedResponse<UserProfileDto> admins = currentUserUtil.buildPaginatedResponse(
                allAdmins,
                offset,
                limit,
                request.getRequestURL().toString(),
                "offset",
                "limit"
        );
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @Operation(
            summary = "Get All User",
            description = "Bring all users who have the User role " +
                    "and no permission for the account that has the user role"
    )
    @Parameters({
            @Parameter(name = "offset", description = "Number of Page", example = "1"),
            @Parameter(name = "size", description = "Number od element", example = "10")
    })
    @GetMapping(path = "/getAllUser")
    public ResponseEntity<PaginatedResponse<UserProfileDto>> getAllUser(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "4") int size,
            HttpServletRequest request
    ){
       Page<UserProfileDto> allUsers = userService.getAllUsers(offset, size);
       PaginatedResponse<UserProfileDto> users = currentUserUtil.buildPaginatedResponse(
               allUsers,
               offset,
               size,
               request.getRequestURL().toString(),
               "offset",
               "size"
       );
       return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<UserProfileDto>> searchByUsername(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "4") int size,
            HttpServletRequest request,
            @RequestParam String name){
        return userService.searchByUsername(name, offset, size, request);
    }

    @Operation(
            summary = "Update Data",
            description = "Updating account data by the user only. He has the right to " +
                    "modify the name, email and phone number."
    )
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "User ID to be updated",
                    example = "5"
            )
    })
    @PutMapping("/updateData/{id}")
    public ResponseEntity<ApiResponse<UpdateComparisonDto>> update(@RequestBody UpdateUserRequest updateUserRequest
            , @PathVariable int id){

         ApiResponse<UpdateComparisonDto> updated = userService.updateUser(updateUserRequest, id);

         return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @Operation(
            summary = "Update Status",
            description = "User status update by admin only"
    )
    @PutMapping("/user/status")
    public ResponseEntity<?> updateStatusByAdmin(@RequestBody StatusUpdateRequest request) {
        return userService.updateStatusByAdmin(request);
    }

    @Operation(
            summary = "Check The Token",
            description = "Check if the token is valid or expired"
    )
    @GetMapping(path = "/checkToken")
    public ResponseEntity<String> checkToken(){
        return userService.checkToken();
    }

}
