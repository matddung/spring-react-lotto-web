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

            // API 요청이 실패한 경우 반복 종료
            if (latestLottoInfo == null || !"success".equals(latestLottoInfo.getReturnValue())) {
                System.out.println("No more data to fetch. Stopping at draw number: " + latestDrawNo);

                // 1133번 회차에서 실패했다면, 마지막 성공 회차 번호인 1132를 저장
                lottoUpdateService.saveLatestDrawNo(latestDrawNo - 1);
                break;
            }

            // 성공적으로 데이터를 가져온 경우, ARFF 파일 업데이트
            boolean updated = wekaService.updateArffFile(latestLottoInfo);

            // 최신 회차 번호 저장
            lottoUpdateService.saveLatestDrawNo(latestDrawNo);

            if (updated) {
                System.out.println("Updated draw number: " + latestDrawNo);
            }

            // 다음 회차 번호로 증가
            latestDrawNo++;
        }
    }
}