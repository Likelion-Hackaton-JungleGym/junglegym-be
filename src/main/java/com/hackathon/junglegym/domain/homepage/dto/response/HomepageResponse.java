package com.hackathon.junglegym.domain.homepage.dto.response;

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
@Schema(title = "HomepageResponse DTO", description = "홈페이지 정보 응답 반환")
public class HomepageResponse {

  @Schema(description = "홈페이지 주소 고유 id", example = "1")
  private Long id;

  @Schema(description = "홈페이지 주소", example = "https://~~")
  private String link;

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
