package com.hackathon.junglegym.domain.promiseCategory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PromiseCategoryResponse DTO", description = "공약 카테고리 응답 반환")
public class PromiseCategoryResponse {

  @Schema(description = "공약 카테고리 ID")
  private Long categoryId;

  @Schema(description = "카테고리 제목")
  private String title;

  @Schema(description = "카테고리 내용")
  private String content;
}
