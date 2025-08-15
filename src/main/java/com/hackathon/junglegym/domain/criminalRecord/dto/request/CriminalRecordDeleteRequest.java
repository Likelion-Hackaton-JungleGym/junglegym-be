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
@Schema(title = "CriminalRecordDeleteRequest DTO", description = "전과 삭제를 위한 데이터 전송")
public class CriminalRecordDeleteRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "전과 제목", example = "무고 공무원자격사칭")
  private String title;
}
