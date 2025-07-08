package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.productDto.GetAllProductResponse;
import com.CafeSystem.cafe.dto.productDto.ProductAddResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {

    ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(ProductDto productDto);

    ResponseEntity<PaginatedResponse<GetAllProductResponse>> getAllProduct(String search,int page, int limit,
                                                                           HttpServletRequest request);
}
