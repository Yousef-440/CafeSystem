package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.dashboard.DetailsResponse;
import com.CafeSystem.cafe.model.Bill;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.model.Product;
import com.CafeSystem.cafe.repository.BillRepository;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.repository.ProductRepository;
import com.CafeSystem.cafe.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BillRepository billRepository;

    @Override
    public ResponseEntity<ApiResponse<DetailsResponse>> getCountDetails() {
        List<Product> allProduct = productRepository.findAll();
        List<Category> allCategory = categoryRepository.findAll();
        List<Bill> allBill = billRepository.findAll();
        log.info("Number of all product in DB is: {}", allProduct.size());
        log.info("Number of all category in DB is: {}", allCategory.size());
        log.info("Number of all bill in DB is: {}", allBill.size());
        DetailsResponse detailsResponse = DetailsResponse.builder()
                .numOfProduct(allProduct.size())
                .numOfCategory(allCategory.size())
                .numOfBill(allBill.size())
                .build();

        ApiResponse response = ApiResponse.builder()
                .status("success")
                .message("all details")
                .data(detailsResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
