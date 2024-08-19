package com.studyjun.lottoweb.config;

import com.studyjun.lottoweb.dto.response.LottoResponse;
import com.studyjun.lottoweb.repository.UserLottoRepository;
import com.studyjun.lottoweb.service.LottoUpdateService;
import com.studyjun.lottoweb.service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledJobs {
    @Autowired
    private UserLottoRepository userLottoRepository;

    @Autowired
    private LottoUpdateService lottoUpdateService;

    @Autowired
    private WekaService wekaService;

    @Scheduled(cron = "0 0 2 ? * SUN")
    @Transactional
    public void cleanUpUserLottoTable() {
        userLottoRepository.deleteAll();
    }

    @Scheduled(cron = "0 0 2 ? * SUN")
    public void updateLottoArff() throws Exception {
        int latestDrawNo = lottoUpdateService.getLatestDrawNo();

        while (true) {
            LottoResponse latestLottoInfo = lottoUpdateService.getLottoInfoByDrawNumber(latestDrawNo);

            if (latestLottoInfo == null || !"success".equals(latestLottoInfo.getReturnValue())) {
                System.out.println("No more data to fetch. Stopping at draw number: " + latestDrawNo);

                lottoUpdateService.saveLatestDrawNo(latestDrawNo - 1);
                break;
            }

            boolean updated = wekaService.updateArffFile(latestLottoInfo);

            lottoUpdateService.saveLatestDrawNo(latestDrawNo);

            if (updated) {
                System.out.println("Updated draw number: " + latestDrawNo);
            }

            latestDrawNo++;
        }
    }
}