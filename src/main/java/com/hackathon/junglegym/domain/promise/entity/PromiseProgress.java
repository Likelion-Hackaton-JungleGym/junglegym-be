package com.hackathon.junglegym.domain.promise.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

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

  @JsonCreator
  public static PromiseProgress fromValue(String value) {
    return switch (value) {
      case "정상추진" -> PromiseProgress.IN_PROGRESS;
      case "일부추진" -> PromiseProgress.IN_PROGRESS_PARTIAL;
      case "완료" -> PromiseProgress.DONE;
      case "이행 후 계속 추진" -> PromiseProgress.ONGOING;
      case "보류" -> PromiseProgress.ON_HOLD;
      case "폐기" -> PromiseProgress.CANCELLED;
      default -> null;
    };
  }
}
