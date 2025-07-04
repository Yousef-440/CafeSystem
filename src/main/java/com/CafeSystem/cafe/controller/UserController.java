package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.service.UserService;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User Controller", description = "Hello Yousef")
@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CurrentUserUtil currentUserUtil;

    @GetMapping(path = "/getAll")
    public ResponseEntity<PaginatedResponse<UserProfileDto>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int limitation,
            HttpServletRequest servletRequest
    ){
        int zeroBasedPage = page - 1;
        Page<UserProfileDto> allPerson = userService.AllUserAndAdmin(zeroBasedPage, limitation);

        PaginatedResponse<UserProfileDto> test = currentUserUtil.buildPaginatedResponse(
                allPerson,
                page,
                limitation,
                servletRequest.getRequestURL().toString(),
                "page","limitation"
        );


        return new ResponseEntity<>(test, HttpStatus.OK);
    }

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

    @PutMapping("/updateData/{id}")
    public ResponseEntity<ApiResponse<UpdateComparisonDto>> update(@RequestBody UpdateUserRequest updateUserRequest
            , @PathVariable int id){

         ApiResponse<UpdateComparisonDto> updated = userService.updateUser(updateUserRequest, id);

         return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping("/user/status")
    public ResponseEntity<?> updateStatusByAdmin(@RequestBody StatusUpdateRequest request) {
        return userService.updateStatusByAdmin(request);
    }

    @GetMapping(path = "/checkToken")
    public ResponseEntity<String> checkToken(){
        return userService.checkToken();
    }

}
