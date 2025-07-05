package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(DtoCategory dtoCategory);

    ResponseEntity<List<GetAllResponse>> getAllCategory();
}
