package com.hackathon.junglegym.domain.dictionary.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "DictionaryDetailResponse DTO", description = "사전 단건 상세 응답")
public class DictionaryDetailResponse {

  @Schema(description = "사전 ID", example = "3")
  private Long id;

  @Schema(description = "키워드", example = "사람보다 당")
  private String keyword;

  @Schema(description = "제목", example = "비례대표 vs 지역구")
  private String title;

  @Schema(description = "부제목", example = "정당 vs 인물, 투표 방식의 차이")
  private String subtitle;

  @Schema(description = "내용", example = "국회의원은 크게 두 가지 방식으로 뽑아요 ...")
  private String content;
}
