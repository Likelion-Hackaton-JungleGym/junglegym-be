package com.hackathon.junglegym.domain.homepage.dto.request;

import com.hackathon.junglegym.domain.homepage.entity.LinkType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "HomepageRequest DTO", description = "홈페이지 생성 및 수정을 위한 데이터 전송")
public class HomepageRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "홈페이지 주소", example = "https://~~")
  private String link;

  @Schema(
      description = "홈페이지 타입",
      example = "인스타그램",
      allowableValues = {"인스타그램", "블로그", "페이스북", "트위터", "유튜브", "홈페이지", "전과링크"})
  private LinkType linkType;
}
