package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.ProductUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUpdateLogRepository extends JpaRepository<ProductUpdateLog, Integer> {

}
