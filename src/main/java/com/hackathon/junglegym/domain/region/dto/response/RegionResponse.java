package com.hackathon.junglegym.domain.region.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "RegionResponse DTO", description = "지역 정보 응답 반환")
public class RegionResponse {

  @Schema(description = "지역 id", example = "1")
  Long id;

  @Schema(description = "지역명", example = "성북구")
  String name;
}
