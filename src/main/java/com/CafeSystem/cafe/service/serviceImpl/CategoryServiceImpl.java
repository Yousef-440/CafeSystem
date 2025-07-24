package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.dto.categoryDto.UpdateCategoryResponseDto;
import com.CafeSystem.cafe.dto.categoryDto.UpdateDataResponse;
import com.CafeSystem.cafe.exception.DuplicateResourceException;
import com.CafeSystem.cafe.exception.ResourceNotFoundException;
import com.CafeSystem.cafe.exception.UnauthorizedException;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.repository.ProductRepository;
import com.CafeSystem.cafe.service.CategoryService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Autowired
    private ProductRepository productRepository;

    private void checkAdminAccess() {
        if (!currentUserUtil.isAdmin()) {
            log.warn("Unauthorized access attempt");
            throw new UnauthorizedException("Unauthorized access");
        }
    }

    @Override
    public ResponseEntity<String> addNewCategory(DtoCategory dtoCategory) {
        log.info("addNewCategory started for name: {}", dtoCategory.getName());

        checkAdminAccess();

        if (categoryRepository.existsByNameIgnoreCase(dtoCategory.getName())) {
            log.warn("Category '{}' already exists", dtoCategory.getName());
            throw new DuplicateResourceException("Category already exists");
        }

        Category category = Category.builder().name(dtoCategory.getName()).build();
        categoryRepository.save(category);

        log.info("Category '{}' added successfully", dtoCategory.getName());
        return CafeUtil.getResponseEntity("Added successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<GetAllResponse>> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();

        List<GetAllResponse> responseList = categories.stream()
                .map(category -> {
                    Long count = productRepository.countProductsByCategoryName(category.getName());
                    return new GetAllResponse(
                            category.getId(),
                            category.getName(),
                            category.getCreatedAt(),
                            category.getModifiedAt(),
                            count
                    );
                })
                .toList();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }


    @Override
    public Page<Category> searchCategories(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return (keyword == null || keyword.trim().isEmpty())
                ? categoryRepository.findAll(pageable)
                : categoryRepository.searchCategory(keyword, pageable);
    }

    @Override
    public ResponseEntity<ApiResponse<UpdateCategoryResponseDto>> update(String name, int id) {
        log.info("Update category started: id={}, newName={}", id, name);

        checkAdminAccess();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found."));

        if (category.getName().equalsIgnoreCase(name)) {
            log.warn("No changes detected. Same name: '{}'", name);
            throw new DuplicateResourceException("Category already has this name.");
        }

        UpdateDataResponse oldData = UpdateDataResponse.builder()
                .name(category.getName())
                .modifiedAt(category.getModifiedAt())
                .build();

        category.setName(name);
        categoryRepository.save(category);

        UpdateDataResponse newData = UpdateDataResponse.builder()
                .name(category.getName())
                .modifiedAt(category.getModifiedAt())
                .build();

        UpdateCategoryResponseDto responseDto = UpdateCategoryResponseDto.builder()
                .oldData(oldData)
                .newData(newData)
                .build();

        ApiResponse<UpdateCategoryResponseDto> response = ApiResponse.<UpdateCategoryResponseDto>builder()
                .status("Success")
                .message("Category updated successfully.")
                .data(responseDto)
                .build();

        log.info("Category updated from '{}' to '{}'", oldData.getName(), newData.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteCategoryById(int id) {
        log.info("deleteCategoryById started for ID: {}", id);

        checkAdminAccess();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        categoryRepository.deleteById(id);

        String message = "Category { " + category.getName().toUpperCase() + " } was deleted successfully";
        log.info(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> numberOfCategory() {
        Integer allCategoryName = categoryRepository.countOfCategory();
        String message = "total number of Categories is: " + allCategoryName;

        ApiResponse response = ApiResponse.builder()
                .status("success")
                .message("count-Of-Category")
                .data(message)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
