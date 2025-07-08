package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.productDto.GetAllProductResponse;
import com.CafeSystem.cafe.dto.productDto.ProductAddResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(@RequestBody ProductDto productDto){
        return productService.addProduct(productDto);
    }

    @GetMapping("/getAll")
    public ResponseEntity<PaginatedResponse<GetAllProductResponse>> getAllProduct(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int limit,
            HttpServletRequest request
    ){
        return productService.getAllProduct(search,page,limit,request);
    }
}
