package com.studyjun.lottoweb.config;

import com.studyjun.lottoweb.repository.UserLottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledJobs {
    @Autowired
    private UserLottoRepository userLottoRepository;

    @Scheduled(cron = "0 0 2 ? * SUN")
    @Transactional
    public void cleanUpUserLottoTable() {
        userLottoRepository.deleteAll();
    }
}
