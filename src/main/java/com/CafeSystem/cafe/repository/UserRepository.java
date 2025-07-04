package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.dto.UserProfileDto;
import com.CafeSystem.cafe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(@Param("email") String email);
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> getAllAdmin();

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    Page<User> getAllAdmin(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role ='USER'")
    Page<User> getAllUser(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update User u set u.status = :status where u.id = :id")
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);


}
