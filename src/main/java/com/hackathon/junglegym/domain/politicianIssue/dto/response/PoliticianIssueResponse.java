package com.hackathon.junglegym.domain.politicianIssue.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PoliticianIssueResponse DTO", description = "정치인 이슈 기사 응답 반환")
public class PoliticianIssueResponse {

  @Schema(description = "정치인 이슈 ID")
  private Long politicianIssueId;

  @Schema(description = "기사 제목")
  private String title;

  @Schema(description = "기사 링크(URL)")
  private String link;
}
