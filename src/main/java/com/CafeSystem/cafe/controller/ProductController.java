package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.productDto.ProductAddResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(@RequestBody ProductDto productDto){
        return productService.addProduct(productDto);
    }
}
