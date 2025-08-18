package com.hackathon.junglegym.domain.activity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "ActivityResponse DTO", description = "정치인 활동 기사 응답 반환")
public class ActivityResponse {

  @Schema(description = "정치인 활동 ID")
  private Long activityId;

  @Schema(description = "기사 제목")
  private String title;

  @Schema(description = "기사 링크(URL)")
  private String link;
}
