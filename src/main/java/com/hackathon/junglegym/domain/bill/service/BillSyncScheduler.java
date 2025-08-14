package com.hackathon.junglegym.domain.bill.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.junglegym.domain.politician.entity.Role;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillSyncScheduler {

  private final BillSyncService billSyncService;
  private final PoliticianRepository politicianRepository;

  // 매일 03:20 (Asia/Seoul)
  @Scheduled(cron = "0 50 0 * * *", zone = "Asia/Seoul")
  public void syncAll() {
    var list =
        politicianRepository.findAll().stream()
            .filter(p -> p.getRole() == Role.NATIONAL_ASSEMBLY)
            .toList();

    int total = 0;
    for (var p : list) {
      try {
        total += billSyncService.syncBillsOfPolitician(p.getId());
      } catch (Exception e) {
        log.error("법률안 동기화 실패 politicianId={}", p.getId(), e);
      }
    }
    log.info("법률안 동기화 총 저장 건수={}", total);
  }
}
