package com.hackathon.junglegym.domain.politicianIssue.service;

import org.springframework.stereotype.Service;

import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliticianIssueScheduler {

  private final PoliticianRepository politicianRepository;
  private final PoliticianIssueSyncService issueSync;

  //  // 매일 04:10 (서울)
  //  @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
  //  public void syncAll() {
  //    var list = politicianRepository.findAll();
  //    int total = 0;
  //    for (var p : list) {
  //      try {
  //        total += issueSync.syncIssues(p.getId());
  //        Thread.sleep(500); // OpenAI/크롤링 과금·부하 조절(선택)
  //      } catch (Exception e) {
  //        log.warn("[ISSUE] sync fail politicianId={}", p.getId(), e);
  //      }
  //    }
  //    log.info("[ISSUE] nightly sync total saved={}", total);
  //  }
}
