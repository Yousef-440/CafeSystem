package com.CafeSystem.cafe.scheduler;

import com.CafeSystem.cafe.model.PasswordResetToken;
import com.CafeSystem.cafe.repository.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class PasswordRestScheduler {
    @Autowired
    private PasswordResetTokenRepository passwordResetToken;

    @Scheduled(cron = "0 0 * * * ? ")
    public void deleteTokenExpire(){
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        List<PasswordResetToken> expiredTokens = passwordResetToken.findAllByExpiryDateBefore(now);
        passwordResetToken.deleteAll(expiredTokens);
        log.info("Deleted {} expired password reset tokens before {}", expiredTokens.size(), now);
    }
}
