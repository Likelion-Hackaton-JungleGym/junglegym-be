package com.hackathon.junglegym.domain.region.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "RegionRequest DTO", description = "지역 생성을 위한 데이터 전송")
public class RegionRequest {

  @Schema(description = "지역명", example = "성북구")
  String name;
}
