package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.*;
import com.CafeSystem.cafe.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface UserService {
    Page<UserProfileDto> AllUserAndAdmin(int page, int limitation);
    Page<UserProfileDto> getAllAdmin(int offset, int size);
    Page<UserProfileDto> getAllUsers(int offset, int size);
    ApiResponse<UpdateComparisonDto> updateUser(UpdateUserRequest userRequest, int id);
    ResponseEntity<String> updateStatusByAdmin(StatusUpdateRequest request);

    ResponseEntity<String> checkToken();
}
