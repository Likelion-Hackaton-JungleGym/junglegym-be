package com.hackathon.junglegym.domain.property.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PropertyResponse DTO", description = "재산 정보 응답 반환")
public class PropertyResponse {

  @Schema(description = "재산 고유 id", example = "1")
  private Long id;

  @Schema(description = "총 자산 (건물/부동산/예금/정치자금 현재가액 합)", example = "5555")
  private Long totalCapital;

  @Schema(description = "총 부채 (채무 현재가액)", example = "3333")
  private Long totalDebt;

  @Schema(description = "순 자산 = 총 자산 - 총 부채", example = "2222")
  private Long capital;
}
