package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.dashboard.DetailsResponse;
import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<ApiResponse<DetailsResponse>> getCountDetails();
}
