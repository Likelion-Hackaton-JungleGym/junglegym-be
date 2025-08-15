package com.hackathon.junglegym.domain.politician.dto.request;

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
@Schema(title = "PoliticianRequest DTO", description = "정치인 생성을 위한 데이터 전송")
public class PoliticianRequest {

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "정치인 이름", example = "김영배")
  private String name;

  @Schema(description = "정당 이름", example = "더불어민주당")
  private String polyName;

  @Schema(description = "역할", example = "NATIONAL_ASSEMBLY(국회의원)")
  private Role role;

  @Schema(description = "소속 위원", example = "외교통일위원회")
  private String committee;

  @Schema(description = "생년월일", example = "1967. 03. 08")
  private String birth;

  @Schema(description = "재선횟수", example = "2")
  private Integer retryNumber;

  @Schema(description = "당선된 선거", example = "제21대, 제22대")
  private String retryUnit;

  @Schema(description = "약력", example = "학력~~ 약력~~")
  private String careerSummary;

  @Schema(description = "병역사항", example = "전시근로역")
  private String military;

  @Schema(description = "역할 이름", example = "서울 성북구갑")
  private String roleName;

  @Schema(description = "관할 지역", example = "성북동, 삼선동, 동선동, 돈암제2동, 안암동, 보문동, 정릉동, 길음제1동")
  private String regionText;
}
