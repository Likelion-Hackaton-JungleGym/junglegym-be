package com.hackathon.junglegym.domain.criminalRecord.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "CriminalRecordRequest DTO", description = "전과 생성을 위한 데이터 전송")
public class CriminalRecordRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "전과 제목", example = "무고 공무원자격사칭")
  private String title;

  @Schema(description = "벌금 (단위: 원)", example = "1500000")
  private Long fine;
}
