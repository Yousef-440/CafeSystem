package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill,Integer> {
    Optional<Bill> findByUuid(String uuid);
}
