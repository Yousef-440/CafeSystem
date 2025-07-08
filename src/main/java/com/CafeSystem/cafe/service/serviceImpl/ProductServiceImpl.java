package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.categoryDto.GetAllResponse;
import com.CafeSystem.cafe.dto.productDto.GetAllProductResponse;
import com.CafeSystem.cafe.dto.productDto.ProductAddResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.model.Product;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.repository.ProductRepository;
import com.CafeSystem.cafe.service.ProductService;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CurrentUserUtil currentUserUtil;
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseEntity<ApiResponse<ProductAddResponse>> addProduct(ProductDto productDto) {
        log.info("AddProduct Function is Started");

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new HandleException("Sorry, category not found"));

        if (!currentUserUtil.isAdmin()) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Unauthorized access attempt by email: {}", email);
            throw new HandleException("Only admins are allowed");
        }

        try {
            Product product = userMapper.toEntity(productDto);
            product.setCategory(category);
            product.setStatus("false");

            productRepository.save(product);

            ProductAddResponse added = ProductAddResponse.builder()
                    .nameProduct(product.getName())
                    .nameCategory(category.getName())
                    .createdAt(product.getCreatedAt())
                    .build();

            String message = product.getName() + " product added successfully!";
            ApiResponse<ProductAddResponse> response = ApiResponse.<ProductAddResponse>builder()
                    .status("success")
                    .message(message)
                    .data(added)
                    .build();

            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException ex) {
            throw new HandleException("The product {" + productDto.getName() + "} already exists in category => " +
                    category.getName());
        } catch (Exception ex) {
            log.error("Unexpected error while adding product", ex);
            throw new HandleException("Something went wrong while adding the product");
        }
    }

    @Override
    public ResponseEntity<PaginatedResponse<GetAllProductResponse>> getAllProduct(
            String search, int page, int limit, HttpServletRequest request
            ) {
        log.info("getAllProduct Function is Started By :{}",
                SecurityContextHolder.getContext().getAuthentication().getName());

        Page<GetAllProductResponse> productResponse;

        Pageable pageable = PageRequest.of(page,limit);


        Page<Product> products = (search != null && !search.trim().isEmpty())
                ? productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable)
                : productRepository.findAll(pageable);

        productResponse = products.map(userMapper::toDto);

        PaginatedResponse<GetAllProductResponse> paginatedResponse = currentUserUtil.buildPaginatedResponse(
                productResponse,
                page,
                limit,
                request.getRequestURL().toString(),
                "page",
                "limit"
        );
            return ResponseEntity.ok(paginatedResponse);
    }
}
