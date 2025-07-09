package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.productDto.*;
import com.CafeSystem.cafe.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping(path = "/updateProduct/{id}")
    public ResponseEntity<ApiResponse<CompareData>>updateData(
            @PathVariable("id")int id,
            @RequestBody UpdateProductRequest productRequest){

        return productService.updateProduct(id,productRequest);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable("id")int id){
        return productService.deleteProduct(id);
    }

    @PutMapping(path = "/updateStatus/{id}")
    public ResponseEntity<ApiResponse<UpdateStatusResponse>> updateStatus(
            @PathVariable("id")int id,
            @RequestBody UpdateStatusRequest request){
        return productService.updateStatus(id,request);
    }

    @GetMapping(path = "/getByCategory/{id}")
    public ResponseEntity<PaginatedResponse<GetAllProductResponse>> getProductsByCategoryId(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit,
            @PathVariable("id") int id,
            HttpServletRequest servletRequest
    ){
        return productService.getProductsByCategoryId(id, offset, limit, servletRequest);
    }
}
