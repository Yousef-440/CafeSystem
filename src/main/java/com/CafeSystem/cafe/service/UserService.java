package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    Page<UserProfileDto> AllUserAndAdmin(int page, int limitation);
    Page<UserProfileDto> getAllAdmin(int offset, int size);
    Page<UserProfileDto> getAllUsers(int offset, int size);
    ResponseEntity<ApiResponse<UpdateComparisonDto>> updateUser(UpdateUserRequest userRequest, int id);
    ResponseEntity<String> updateStatusByAdmin(StatusUpdateRequest request);

    ResponseEntity<String> checkToken();

    ResponseEntity<PaginatedResponse<UserProfileDto>> searchByUsername(String name, int offset,int size, HttpServletRequest request);
}
