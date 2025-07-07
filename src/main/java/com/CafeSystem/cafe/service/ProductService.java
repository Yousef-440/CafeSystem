package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.productDto.ProductAddResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(ProductDto productDto);
}
