package com.CafeSystem.cafe.controller;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.categoryDto.DtoCategory;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.dto.categoryDto.UpdateCategoryRequest;
import com.CafeSystem.cafe.dto.categoryDto.UpdateCategoryResponseDto;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.service.CategoryService;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/category")
@Tag(name = "Category Controller", description = "It is a controller that " +
        "contains all the processes that belong to Category")

public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Operation(
            summary = "add categories",
            description = "Add products and for every product" +
                    " that is added, no other product will be added."
    )
    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory(@Valid @RequestBody DtoCategory dtoCategory){
        try {
            return categoryService.addNewCategory(dtoCategory);
        }catch (Exception ex){
            return CafeUtil.getResponseEntity("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "get all categories"
    )
    @GetMapping(path = "/getAll")
    public ResponseEntity<List<GetAllResponse>> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @Operation(
            summary = "search the categories",
            description ="Search for products so that the user enters a product," +
                    " whether it is capital or small letters."
    )
    @GetMapping(path = "/search")
    public ResponseEntity<PaginatedResponse<Category>> searchCategory(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit,
            HttpServletRequest request
    ) {
        try {
            Page<Category> categories = categoryService.searchCategories(offset, limit, keyword);
            String currentUrl = request.getRequestURL().toString();

            String queryFormat = "%s?keyword=%s&%s=%d&%s=%d";

            String nextUrl = categories.hasNext()
                    ? String.format(queryFormat, currentUrl, keyword, "offset", offset + 1, "limit", limit)
                    : null;

            String prevUrl = categories.hasPrevious()
                    ? String.format(queryFormat, currentUrl, keyword, "offset", offset - 1, "limit", limit)
                    : null;

            PaginatedResponse<Category> category = PaginatedResponse.<Category>builder()
                    .content(categories.getContent())
                    .currentPage(categories.getNumber() + 1)
                    .totalPages(categories.getTotalPages())
                    .totalItems(categories.getTotalElements())
                    .hasNext(categories.hasNext())
                    .hasPrevious(categories.hasPrevious())
                    .nextPageURL(nextUrl)
                    .prevPageURL(prevUrl)
                    .build();

            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception ex) {
            throw new HandleException("Something Went Wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Update Category",
            description = "Updated the Category by Admin"
    )
    @PutMapping(path = "/update/{id}")
    public ResponseEntity<ApiResponse<UpdateCategoryResponseDto>> updateCategory(
            @PathVariable("id") int id, @RequestBody UpdateCategoryRequest request){
        return categoryService.update(request.getName(),id);
    }

    @Operation(
            summary = "Delete Category"
    )
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id")int id){
        return categoryService.deleteCategoryById(id);
    }
}
