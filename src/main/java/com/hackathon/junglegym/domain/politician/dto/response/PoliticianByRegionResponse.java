package com.hackathon.junglegym.domain.politician.dto.response;

import com.hackathon.junglegym.domain.politician.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "PoliticianByRegionResponse DTO", description = "지역별 정치인 목록 응답 반환")
public class PoliticianByRegionResponse {

  @Schema(description = "정치인 고유 번호", example = "1")
  private Long id;

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "정당 이름", example = "더불어민주당")
  private String polyName;

  @Schema(description = "역할", example = "NATIONAL_ASSEMBLY(국회의원)")
  private Role role;

  @Schema(description = "프로필 사진", example = "url 주소")
  private String profileImg;

  @Schema(description = "역할 이름", example = "서울 성북구갑")
  private String roleName;
}
