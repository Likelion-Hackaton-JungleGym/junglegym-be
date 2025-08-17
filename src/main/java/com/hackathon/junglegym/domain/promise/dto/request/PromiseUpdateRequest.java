package com.hackathon.junglegym.domain.promise.dto.request;

import com.hackathon.junglegym.domain.promise.entity.PromiseProgress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PromiseUpdateRequest DTO", description = "공약 수정을 위한 데이터 전송")
public class PromiseUpdateRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String politicianName;

  @Schema(description = "카테고리 제목", example = "함께 나눔 복지도시")
  private String category;

  @Schema(description = "공약명", example = "정릉종합사회복지관 이전 건립 추진")
  private String name;

  @Schema(description = "공약명", example = "정릉종합사회복지관 이전 건립 추진")
  private String newName;

  @Schema(
      description = "공약 진행상황",
      example = "정상추진",
      allowableValues = {"정상추진", "일부추진", "완료", "이행 후 계속 추진", "보류", "폐기"})
  private PromiseProgress newProgress;

  @Schema(description = "공약 목표(뒷면)", example = "정릉종합사회복지관을 이전 및 건립하여 정릉 지역 주민의 다양한 복지 문제예방과 욕구 해결")
  private String newGoal;
}
