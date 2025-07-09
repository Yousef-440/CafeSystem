package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.ApiResponse;
import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.dto.productDto.*;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.mapper.UserMapper;
import com.CafeSystem.cafe.model.Category;
import com.CafeSystem.cafe.model.Product;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.repository.ProductRepository;
import com.CafeSystem.cafe.repository.UserRepository;
import com.CafeSystem.cafe.service.ProductService;
import com.CafeSystem.cafe.service.email.EmailService;
import com.CafeSystem.cafe.utils.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


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
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

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

    @Override
    public ResponseEntity<ApiResponse<CompareData>> updateProduct(int id, UpdateProductRequest productRequest) {
        log.info("Update request received for product ID: {}", id);

        if(!currentUserUtil.isAdmin()){
            throw new HandleException("Only admins are allowed");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(()->
                {
                    log.warn("Product with ID {} not found", id);
                    return new HandleException("Sorry, Product Not Found");
                });

        ProductUpdateResponse oldData = userMapper.convert(product);

        boolean isUpdated = false;

        if (productRequest.getName() != null) {
            log.info("Updating product name from '{}' to '{}'",
                    product.getName(), productRequest.getName());
            product.setName(productRequest.getName());
            isUpdated = true;
        }

        if (productRequest.getDescription() != null) {
            log.info("Updating product description from '{}' to '{}'",
                    product.getDescription(), productRequest.getDescription());
            product.setDescription(productRequest.getDescription());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new HandleException("No data provided to update");
        }
        productRepository.save(product);
        log.info("Product with ID {} updated successfully", id);

        ProductUpdateResponse newData = userMapper.convert(product);

        CompareData compareData = CompareData.builder()
                .oldData(oldData)
                .newData(newData)
                .build();

        ApiResponse<CompareData> response = ApiResponse.<CompareData>builder()
                .status("Success")
                .message("Update Product successfully")
                .data(compareData)
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<String>> deleteProduct(int id) {
        log.info("DeleteProduct function started by user: {}",
                SecurityContextHolder.getContext().getAuthentication().getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new HandleException("Not Found"));

        if (!currentUserUtil.isAdmin()) {
            log.warn("Unauthorized delete product");
            throw new HandleException("Only admins are allowed");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("product By id {} not found", id);
                    return new HandleException("Sorry, id Not Found");
                });

        productRepository.deleteById(id);
        log.info("Product with ID {} and name '{}' deleted successfully.", id, product.getName());

        String str = "The product \"" + product.getName() + "\" has been successfully deleted.";

        ApiResponse<String> response = ApiResponse.<String>builder()
                .status("success")
                .message("Product deleted successfully!")
                .data(str)
                .build();
        String subject = "Delete Product";
        String message = "Dear Admins,\n\n" +
                "Please be informed that the product \"" + product.getName() + "\" (ID: " + product.getId() + ") " +
                "was deleted by admin \"" + user.getName() + "\".\n\n" +
                "If this action was not intended or you have any concerns, please review the system logs or contact the system administrator.\n\n" +
                "Admin Details:\n" +
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n\n" +
                "Regards,\n" +
                "Cafe System";

        List<User> getAllAdmin = userRepository.getAllAdmin();
        List<User> admins = getAllAdmin.stream()
                .filter(admin -> !admin.getEmail().equals(authentication.getName()))
                .toList();

        emailService.sendEmailToAdmins(subject,message,admins);
        return ResponseEntity.ok(response);
    }
}
