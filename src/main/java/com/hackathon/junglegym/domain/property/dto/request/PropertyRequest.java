package com.hackathon.junglegym.domain.property.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PropertyRequest DTO", description = "재산 생성을 위한 데이터 전송")
public class PropertyRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "총 자산 (건물/부동산/예금/정치자금 현재가액 합)", example = "5555")
  private Long totalCapital;

  @Schema(description = "총 부채 (채무 현재가액)", example = "3333")
  private Long totalDebt;

  @Schema(description = "순 자산 = 총 자산 - 총 부채", example = "2222")
  private Long capital;
}
