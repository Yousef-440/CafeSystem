package com.CafeSystem.cafe.service;

import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(DtoCategory dtoCategory);

    ResponseEntity<List<GetAllResponse>> getAllCategory();

    Page<Category> searchCategories(int offset,int limit,String keyword);
}
