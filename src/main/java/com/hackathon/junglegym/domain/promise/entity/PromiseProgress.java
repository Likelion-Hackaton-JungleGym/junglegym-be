package com.hackathon.junglegym.domain.promise.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromiseProgress {
  IN_PROGRESS("정상추진"),
  IN_PROGRESS_PARTIAL("일부추진"),
  DONE("완료"),
  ONGOING("이행 후 계속 추진"),
  ON_HOLD("보류"),
  CANCELLED("폐기");

  private final String description; // 한글 설명

  // ex
  // 영문 상수명
  // System.out.println(progress.name()); // IN_PROGRESS

  // 한글 설명
  // System.out.println(progress.getDescription()); // 정상추진
}
