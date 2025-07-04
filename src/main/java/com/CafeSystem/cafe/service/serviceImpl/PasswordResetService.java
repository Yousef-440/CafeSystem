package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.model.PasswordResetToken;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public String createRestToken(User user){
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 15));

        passwordResetTokenRepository.save(resetToken);
        return token;
    }
}
