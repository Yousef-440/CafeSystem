package com.CafeSystem.cafe.scheduler;

import com.CafeSystem.cafe.enumType.StatusType;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class UserCleanupPendingStatusService {
    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deletePendingUsersOlderThan24Hours(){
        log.info("deletePendingUsersOlderThan24Hours function is started");

        LocalDateTime time = LocalDateTime.now().minusHours(24);
        List<User> usersToDelete = userRepository.findByStatusAndCreatedAtBefore(StatusType.PENDING, time);

        userRepository.deleteAll(usersToDelete);

        log.info("Deleted " + usersToDelete.size() + " pending users older than 24 hours.");
    }
}
