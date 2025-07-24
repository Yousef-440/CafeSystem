package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    boolean existsByNameIgnoreCase(@Param("name") String name);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String description,
            Pageable pageable
    );

    Page<Product> findAllByCategory_Id(int categoryId, Pageable pageable);


    @Query("SELECT COUNT(p.id) FROM Product p WHERE p.category.name = :categoryName")
    Long countProductsByCategoryName(@Param("categoryName") String categoryName);



}
