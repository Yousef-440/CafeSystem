package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.service.CategoryService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
