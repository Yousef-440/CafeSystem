package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.bill.BillRequestDTO;
import com.CafeSystem.cafe.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bill")
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping(path = "/generateReport")
    public ResponseEntity<String> generateReport(
            @Valid @RequestBody BillRequestDTO requestDTO,
            @RequestParam(required = false, defaultValue = "true")  Boolean isGenerate
    ){
        return billService.generateReport(requestDTO, isGenerate);
    }
}
