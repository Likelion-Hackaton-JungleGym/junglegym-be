package com.hackathon.junglegym.domain.activity.service;

import org.springframework.stereotype.Service;

import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityScheduler {

  private final PoliticianRepository politicianRepository;
  private final ActivitySyncService activitySyncService;

  //  // 매일 04:20 (서울)
  //  @Scheduled(cron = "0 20 4 * * *", zone = "Asia/Seoul")
  //  public void nightlySync() {
  //    var list = politicianRepository.findAll();
  //    int ok = 0, fail = 0;
  //
  //    // 소규모 병렬 (예: 3~5) + 약간의 슬리프로 페이싱
  //    list.parallelStream().limit(5).forEach(p -> {}); // 단, 간단히 쓰기 어렵다면 순차 + Thread.sleep 도 OK
  //
  //    for (var p : list) {
  //      try {
  //        activitySyncService.syncActivities(p.getId());
  //        ok++;
  //        Thread.sleep(300); // 외부 호출 페이싱 (선택)
  //      } catch (Exception e) {
  //        fail++;
  //        log.warn("[ACT] nightly sync fail id={}", p.getId(), e);
  //      }
  //    }
  //    log.info("[ACT] nightly sync done ok={}, fail={}", ok, fail);
  //  }
}
