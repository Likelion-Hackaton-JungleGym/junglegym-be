package com.hackathon.junglegym.domain.politicianIssue.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.politicianIssue.dto.response.PoliticianIssueResponse;
import com.hackathon.junglegym.domain.politicianIssue.entity.PoliticianIssue;
import com.hackathon.junglegym.domain.politicianIssue.mapping.PoliticianIssueMapper;
import com.hackathon.junglegym.domain.politicianIssue.repository.PoliticianIssueRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliticianIssueService {

  private final PoliticianRepository politicianRepository;
  private final PoliticianIssueRepository issueRepository;
  private final PoliticianIssueSyncService syncService; // 수집 전용 서비스 의존

  // 하루 1회만 새로 수집하고 기존 3건은 삭제 후 저장
  @Transactional
  public List<PoliticianIssueResponse> getIssuesDaily(Long politicianId) throws Exception {
    Politician p =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    // 우선 빠르게 최신 3건 조회 (락 없이): 있으면 바로 반환
    List<PoliticianIssue> latest = issueRepository.findTop3ByPoliticianOrderByCreatedAtDesc(p);
    boolean alreadyToday =
        latest.stream()
            .anyMatch(
                e ->
                    e.getCreatedAt() != null
                        && e.getCreatedAt().toLocalDate().isEqual(LocalDate.now()));
    if (alreadyToday && latest.size() == 3) {
      return PoliticianIssueMapper.toResponseList(latest);
    }

    // 분산락 시도 (최대 5초 대기)
    Integer got = issueRepository.tryLock(politicianId, 5);
    if (got == null || got != 1) {
      // 누가 갱신 중 → 현재 저장된 것 반환
      List<PoliticianIssue> top3 = issueRepository.findTop3ByPoliticianOrderByCreatedAtDesc(p);
      return PoliticianIssueMapper.toResponseList(top3);
    }

    try {
      // *** 경쟁조건 방지: 락을 잡은 후 '오늘 갱신 유무'를 다시 확인 ***
      List<PoliticianIssue> latest2 = issueRepository.findTop3ByPoliticianOrderByCreatedAtDesc(p);
      boolean alreadyToday2 =
          latest2.stream()
              .anyMatch(
                  e ->
                      e.getCreatedAt() != null
                          && e.getCreatedAt().toLocalDate().isEqual(LocalDate.now()));
      if (!alreadyToday2) {
        // 기존 3건 소프트 삭제(@SQLDelete로 is_deleted=true)
        if (!latest2.isEmpty()) {
          issueRepository.deleteAll(latest2);
        }
        // 새로 3건 수집/저장
        int saved = syncService.syncIssues(politicianId);
        log.info("[ISSUE] refreshed politicianId={}, saved={}", politicianId, saved);
      }

      // 최종 조회(최신 3건)
      List<PoliticianIssue> top3 = issueRepository.findTop3ByPoliticianOrderByCreatedAtDesc(p);
      return PoliticianIssueMapper.toResponseList(top3);

    } finally {
      // 락 해제 (예외여도 반드시)
      issueRepository.releaseLock(politicianId);
    }
  }
}
