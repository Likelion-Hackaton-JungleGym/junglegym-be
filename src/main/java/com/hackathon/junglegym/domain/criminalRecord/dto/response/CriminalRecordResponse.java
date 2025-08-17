package com.hackathon.junglegym.domain.criminalRecord.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "CriminalRecordResponse DTO", description = "전과 정보 응답 반환")
public class CriminalRecordResponse {

  @Schema(description = "전과 고유 id", example = "1")
  private Long id;

  @Schema(description = "전과 제목", example = "무고 공무원자격사칭")
  private String title;

  @Schema(description = "벌금 (단위: 원)", example = "1500000")
  private Long fine;
}
