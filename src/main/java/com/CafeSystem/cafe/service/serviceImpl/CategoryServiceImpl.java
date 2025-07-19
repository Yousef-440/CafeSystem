package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.dto.categoryDto.UpdateCategoryResponseDto;
import com.CafeSystem.cafe.dto.categoryDto.UpdateDataResponse;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.repository.CategoryRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public ResponseEntity<String> addNewCategory(DtoCategory dtoCategory) {
        log.info("addNewCategory function started");
        try {
            if (currentUserUtil.isAdmin()) {

                boolean isExists = categoryRepository.existsByNameIgnoreCase(dtoCategory.getName());
                if (isExists) {
                    log.warn("Category with name {} is already exists", dtoCategory.getName());
                    return CafeUtil.getResponseEntity("Category already exists", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                Category category = Category.builder().name(dtoCategory.getName()).build();
                categoryRepository.save(category);
                log.info("Category Add successfully");
                return CafeUtil.getResponseEntity("Added successfully", HttpStatus.OK);
            }else{
                log.warn("Current user does not have permission to add a category.");
                return CafeUtil.getResponseEntity("Unauthorized Access", HttpStatus.FORBIDDEN);
            }
        }catch (Exception ex){
            log.error("Error occurred while adding category '{}'", dtoCategory.getName(), ex);
            return CafeUtil.getResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<GetAllResponse>> getAllCategory() {
        List<Category> all = categoryRepository.findAll();

        List<GetAllResponse> allDto = all.stream()
                .map(category ->new GetAllResponse(
                        category.getId(),
                        category.getName(),
                        category.getCreatedAt(),
                        category.getModifiedAt()))
                .toList();

        return new ResponseEntity<>(allDto, HttpStatus.OK);
    }

    @Override
    public Page<Category> searchCategories(int offset,int limit,String keyword) {
        Pageable pageable = PageRequest.of(offset, limit);
        if (keyword == null || keyword.trim().isEmpty()){
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.searchCategory(keyword,pageable);
    }
    @Override
    public ResponseEntity<ApiResponse<UpdateCategoryResponseDto>> update(String name, int id) {
        log.info("Update Function is Started");

        if (!currentUserUtil.isAdmin()) {
            log.warn("Unauthorized access to update category.");
            throw new HandleException("Unauthorized access");
        }

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new HandleException("Category with ID " + id + " not found.")
        );

        log.info("Category found: {}", category.getName());

        if (category.getName().equalsIgnoreCase(name)) {
            log.warn("No changes detected. Category name is already '{}'", name);
            throw new HandleException("Category already has this name.");
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
        log.info("deleteCategory Function Started");
        if(!currentUserUtil.isAdmin()){
            log.warn("Unauthorized access");
            throw new HandleException("Unauthorized access");
        }
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new HandleException("Category Not Found")
        );

        categoryRepository.deleteById(id);
        String str = "Category By { " + category.getName().toUpperCase() + " } Was Deleted Successfully";
        log.info(str);
        return new ResponseEntity<>(str,HttpStatus.OK);
    }
}
