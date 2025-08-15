package com.hackathon.junglegym.domain.promise.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
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
@Schema(title = "PromiseResponse: 카테고리 클릭 시 공약 리스트 응답")
public class PromiseResponse {

  @Schema(description = "공약 ID")
  private Long promiseId;

  @Schema(description = "공약명")
  private String name;

  @Schema(description = "공약 진행상황")
  private PromiseProgress progress;

  @Schema(description = "공약 목표(뒷면)")
  private String goal;

  @JsonGetter("progressLabel")
  @Schema(description = "공약 진행상황 한글 설명", example = "정상추진")
  public String getProgressLabel() {
    return progress != null ? progress.getDescription() : null;
  }
}
