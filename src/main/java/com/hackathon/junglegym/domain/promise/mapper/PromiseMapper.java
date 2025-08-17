package com.hackathon.junglegym.domain.promise.mapper;

import java.time.LocalDate;
import java.util.List;

import com.hackathon.junglegym.domain.promise.dto.request.PromiseRequest;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseProgressSummaryResponse;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.entity.Promise;
import com.hackathon.junglegym.domain.promise.entity.PromiseProgress;
import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;

public class PromiseMapper {

  public static PromiseProgressSummaryResponse toProgressSummary(List<Promise> promises) {
    int totalCount = promises.size();

    // 공약 상태 개수 세기
    int doneCount = countByProgress(promises, PromiseProgress.DONE);
    int ongoingCount = countByProgress(promises, PromiseProgress.ONGOING);
    int inProgressNormalCount = countByProgress(promises, PromiseProgress.IN_PROGRESS);
    int inProgressPartialCount = countByProgress(promises, PromiseProgress.IN_PROGRESS_PARTIAL);
    int onHoldCount = countByProgress(promises, PromiseProgress.ON_HOLD);
    int cancelledCount = countByProgress(promises, PromiseProgress.CANCELLED);

    // 소계 계산
    int completedSubtotal = doneCount + ongoingCount;
    int inProgressSubtotal = inProgressNormalCount + inProgressPartialCount;
    int notImplementedSubtotal = onHoldCount + cancelledCount;

    return PromiseProgressSummaryResponse.builder()
        .totalCount(totalCount)
        .doneCount(doneCount)
        .ongoingCount(ongoingCount)
        .inProgressNormalCount(inProgressNormalCount)
        .inProgressPartialCount(inProgressPartialCount)
        .onHoldCount(onHoldCount)
        .cancelledCount(cancelledCount)
        .completedSubtotal(completedSubtotal)
        .inProgressSubtotal(inProgressSubtotal)
        .notImplementedSubtotal(notImplementedSubtotal)
        .doneRate(calcRate(doneCount, totalCount)) // 비율 계산
        .ongoingRate(calcRate(ongoingCount, totalCount))
        .inProgressNormalRate(calcRate(inProgressNormalCount, totalCount))
        .inProgressPartialRate(calcRate(inProgressPartialCount, totalCount))
        .onHoldRate(calcRate(onHoldCount, totalCount))
        .cancelledRate(calcRate(cancelledCount, totalCount))
        .completedSubtotalRate(calcRate(completedSubtotal, totalCount))
        .inProgressSubtotalRate(calcRate(inProgressSubtotal, totalCount))
        .notImplementedSubtotalRate(calcRate(notImplementedSubtotal, totalCount))
        .updatedDate(getLatestUpdatedDate(promises))
        .build();
  }

  // 전달받은 promises 리스트에서 특정 progress(상태값)에 해당하는 공약 개수를 세는 메서드
  private static int countByProgress(List<Promise> promises, PromiseProgress progress) {
    return (int)
        promises.stream() // stream()으로 리스트 순회
            .filter(
                p -> p.getProgress() == progress) // p.getProgress() == progress인 것만 filter()로 골라내기
            .count();
  }

  // 전체 개수(total) 대비 특정 개수(count)가 차지하는 비율(%) 구하는 로직
  private static int calcRate(int count, int total) {
    if (total == 0) { // 0 나누기 방지
      return 0;
    }
    return Math.round((count * 100f) / total);
  }

  // 가장 최근 날짜를 max()로 찾아 반환하는 메서드
  private static LocalDate getLatestUpdatedDate(List<Promise> promises) {
    return promises.stream()
        .map(p -> p.getUpdatedAt().toLocalDate()) // updatedAt이 LocalDateTime이라고 가정
        .max(LocalDate::compareTo) // 날짜 비교
        .orElse(null); // 데이터 없으면 null 로 반환
  }

  // Entity -> Response
  public static PromiseResponse toPromiseResponse(Promise promise) {
    return PromiseResponse.builder()
        .promiseId(promise.getId())
        .name(promise.getName())
        .progress(promise.getProgress())
        .goal(promise.getGoal())
        .build();
  }

  // Request -> Entity
  public static Promise toPromise(PromiseRequest request, PromiseCategory promiseCategory) {
    return Promise.builder()
        .category(promiseCategory)
        .name(request.getName())
        .progress(request.getProgress())
        .goal(request.getGoal())
        .build();
  }
}
