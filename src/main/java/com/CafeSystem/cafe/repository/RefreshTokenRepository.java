package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.RefreshToken;
import com.CafeSystem.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}
