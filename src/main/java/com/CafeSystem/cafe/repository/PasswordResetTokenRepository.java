package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findAllByExpiryDateBefore(LocalDateTime time);

}
