package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
