package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.enumType.StatusType;
import com.CafeSystem.cafe.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> getAllAdmin();

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    Page<User> getAllAdmin(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role ='USER'")
    Page<User> getAllUser(Pageable pageable);


    @Transactional
    @Modifying
    @Query("update User u set u.status = :status where u.id = :id")
    void updateStatus(@Param("status") String status, @Param("id") Integer id);


    List<User> findByStatusAndCreatedAtBefore(StatusType statusType, LocalDateTime time);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> searchByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
