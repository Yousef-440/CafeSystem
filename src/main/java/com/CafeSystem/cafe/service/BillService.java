package com.CafeSystem.cafe.service;


import com.CafeSystem.cafe.dto.bill.BillRequestDTO;
import org.springframework.http.ResponseEntity;


public interface BillService {
    ResponseEntity<String> generateReport(BillRequestDTO billRequestDTO,boolean isGenerate);
}
