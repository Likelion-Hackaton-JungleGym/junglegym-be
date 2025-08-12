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
@Schema(title = "RegionUpdateRequest DTO", description = "지역 정보 수정을 위한 데이터 전송")
public class RegionUpdateRequest {

  @Schema(description = "기존 지역명", example = "샤북구")
  String name;

  @Schema(description = "수정한 지역명", example = "성북구")
  String newName;
}
