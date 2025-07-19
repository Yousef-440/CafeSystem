package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.dashboard.DetailsResponse;
import com.CafeSystem.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/dashboard")
public class Dashboard {
    @Autowired
    private DashboardService service;

    @RequestMapping(path = "/details")
    public ResponseEntity<ApiResponse<DetailsResponse>> getCount(){
        return service.getCountDetails();
    }
}
