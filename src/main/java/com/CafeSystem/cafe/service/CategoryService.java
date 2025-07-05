package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(DtoCategory dtoCategory);
}
