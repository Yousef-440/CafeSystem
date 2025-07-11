package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.productDto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {

    ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(ProductDto productDto);

    ResponseEntity<PaginatedResponse<GetAllProductResponse>> getAllProduct(String search,int page, int limit,
                                                                           HttpServletRequest request);

    ResponseEntity<ApiResponse<CompareData>> updateProduct(int id, UpdateProductRequest productRequest);

    ResponseEntity<ApiResponse<String>> deleteProduct(int id);

    ResponseEntity<ApiResponse<UpdateStatusResponse>> updateStatus(int id, UpdateStatusRequest request);

    ResponseEntity<PaginatedResponse<GetAllProductResponse>> getProductsByCategoryId(int id, int offset, int limit,
                                                     HttpServletRequest servletRequest);
}
