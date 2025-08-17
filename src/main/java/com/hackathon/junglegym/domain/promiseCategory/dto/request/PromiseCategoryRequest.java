package com.hackathon.junglegym.domain.promiseCategory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PromiseCategoryRequest DTO", description = "공약 카테고리 생성을 위한 데이터 전송")
public class PromiseCategoryRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "카테고리 제목", example = "함께 나눔 복지도시")
  private String title;

  @Schema(description = "카테고리 내용", example = "촘촘하고 건강한 보편적 복지를 일상에서 보장하겠습니다.")
  private String content;
}
