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
@Schema(title = "HomepageDeleteRequest DTO", description = "홈페이지 삭제를 위한 데이터 전송")
public class HomepageDeleteRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(
      description = "홈페이지 타입",
      example = "INSTAGRAM",
      allowableValues = {
        "INSTAGRAM",
        "BLOG",
        "FACEBOOK",
        "TWITTER",
        "YOUTUBE",
        "HOMEPAGE",
        "FINELINK"
      })
  private LinkType linkType;
}
