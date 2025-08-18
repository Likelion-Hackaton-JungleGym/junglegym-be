package com.hackathon.junglegym.domain.activity.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.activity.dto.response.ActivityResponse;
import com.hackathon.junglegym.domain.activity.entity.Activity;
import com.hackathon.junglegym.domain.activity.mapper.ActivityMapper;
import com.hackathon.junglegym.domain.activity.repository.ActivityRepository;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

  private final ActivityRepository activityRepository;
  private final PoliticianRepository politicianRepository;
  private final ActivitySyncService activitySyncService;

  @Transactional
  public List<ActivityResponse> getActivitiesDaily(Long politicianId) throws Exception {
    Politician p =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    // 1) 락 없이 빠르게 최신 N건 확인: 오늘자 있고 3건이면 바로 리턴
    List<Activity> latest = activityRepository.findTop50ByPoliticianOrderByCreatedAtDesc(p);
    boolean todayReady =
        latest.stream()
            .anyMatch(
                a ->
                    a.getCreatedAt() != null
                        && a.getCreatedAt().toLocalDate().isEqual(LocalDate.now()));
    if (todayReady && latest.size() >= 3) {
      return ActivityMapper.toActivityResponseList(latest.stream().limit(3).toList());
    }

    // 2) 분산락 시도(최대 5초 대기)
    Integer got = activityRepository.tryLock(politicianId, 5);
    if (got == null || got != 1) {
      // 누가 갱신 중이면 현재 저장본 반환
      return ActivityMapper.toActivityResponseList(
          activityRepository.findTop50ByPoliticianOrderByCreatedAtDesc(p).stream()
              .limit(3)
              .toList());
    }

    try {
      // 3) 락 잡은 뒤 다시 확인(경쟁 조건 방지)
      List<Activity> latest2 = activityRepository.findTop50ByPoliticianOrderByCreatedAtDesc(p);
      boolean todayReady2 =
          latest2.stream()
              .anyMatch(
                  a ->
                      a.getCreatedAt() != null
                          && a.getCreatedAt().toLocalDate().isEqual(LocalDate.now()));
      if (!todayReady2) {
        // 새로 수집/저장 (중복 링크 upsert 되므로 삭제 불필요)
        int saved = activitySyncService.syncActivities(politicianId);
        log.info("[ACT] refreshed politicianId={}, saved={}", politicianId, saved);
      }
      return ActivityMapper.toActivityResponseList(
          activityRepository.findTop50ByPoliticianOrderByCreatedAtDesc(p).stream()
              .limit(3)
              .toList());
    } finally {
      activityRepository.releaseLock(politicianId);
    }
  }
}
