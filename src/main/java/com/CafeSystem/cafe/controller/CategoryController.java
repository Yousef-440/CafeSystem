package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.service.CategoryService;
import com.CafeSystem.cafe.utils.CafeUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory(@Valid @RequestBody DtoCategory dtoCategory){
        try {
            return categoryService.addNewCategory(dtoCategory);
        }catch (Exception ex){
            return CafeUtil.getResponseEntity("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<List<GetAllResponse>> getAllCategory(){
        return categoryService.getAllCategory();
    }

}
