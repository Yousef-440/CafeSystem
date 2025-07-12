package com.CafeSystem.cafe.scheduler;

import com.CafeSystem.cafe.repository.ProductUpdateLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DataCleanupScheduler {
    @Autowired
    private ProductUpdateLogRepository repository;

    @Scheduled(cron = "0 30 0 15 * ?")
    public void deleteOldData(){
        log.info("DataCleanupScheduler Started");
        LocalDateTime fifteenMonthsAgo = LocalDateTime.now().minusMonths(15);
        int numOfRows = repository.deleteByUpdateTimeBefore(fifteenMonthsAgo);

        log.info((numOfRows >= 1)
                ? "The number of deleted records is: {}"
                : "There are no records to delete" , numOfRows);
    }
}
