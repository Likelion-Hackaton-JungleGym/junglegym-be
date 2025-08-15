package com.hackathon.junglegym.domain.politician.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PoliticianInfoScheduler {

  public final PoliticianInfoSync svc;

  // 매주 일요일 0시에 실행
  @Scheduled(cron = "0 0 17 ? * 5", zone = "Asia/Seoul")
  public void politicianInfo() {
    try {
      int n = svc.syncAllByRegions();
      log.info("정치인 정보 크롤링 및 저장 성공: {}행", n);

    } catch (Exception e) {
      log.error("정치인 정보 크롤링 및 저장 실패", e);
    }
  }
}
