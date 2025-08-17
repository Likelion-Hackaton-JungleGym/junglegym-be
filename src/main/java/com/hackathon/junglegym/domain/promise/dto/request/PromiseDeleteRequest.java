package com.hackathon.junglegym.domain.promise.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PromiseDeleteRequest DTO", description = "공약 삭제를 위한 데이터 전송")
public class PromiseDeleteRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String politicianName;

  @Schema(description = "카테고리 제목", example = "함께 나눔 복지도시")
  private String category;

  @Schema(description = "공약명", example = "정릉종합사회복지관 이전 건립 추진")
  private String name;
}
