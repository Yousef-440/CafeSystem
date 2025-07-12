package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.ProductUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ProductUpdateLogRepository extends JpaRepository<ProductUpdateLog, Integer> {

    @Transactional
    @Modifying
    int deleteByUpdateTimeBefore(@Param("updateTime") LocalDateTime updateTime);
}
