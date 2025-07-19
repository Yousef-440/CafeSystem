package com.CafeSystem.cafe.repository;

import com.CafeSystem.cafe.model.UserVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationTokenRepository extends JpaRepository<UserVerificationToken, Integer> {
    Optional<UserVerificationToken> findByToken(String token);
}
