package com.hackathon.junglegym.domain.dictionary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "DictionaryRequest DTO", description = "정글 사전 등록을 위한 데이터 전송")
public class DictionaryCreateRequest {

  @Schema(description = "키워드", example = "사람보다 당")
  private String keyword;

  @Schema(description = "제목", example = "비례대표 vs 지역구")
  private String title;

  @Schema(description = "부제목", example = "정당 vs 인물, 투표 방식의 차이")
  private String subtitle;

  @Schema(
      description = "내용",
      example =
          "국회의원은 크게 두 가지 방식으로 뽑아요.\\n\\n**지역구 의원은 ‘동네 대표’예요.** 주민들이 특정 후보를 직접 선택하고, 뽑힌 의원은 도로, 복지, 청년 정책 등 생활과 밀접한 지역 문제를 챙겨요. 인구가 많은 지역은 갑·을처럼 여러 선거구로 나누어 각각 의원을 뽑기도 합니다.\\n\\n반대로 **비례대표 의원은 ‘정당 성적표’로 뽑아요.**\\n정당이 얻은 투표율에 따라 의석을 배분하고, 정당이 정한 순서대로 당선자를 결정해요. 이들은 여성, 청년, 장애인, 환경, 노동 등 특정 분야 전문가로 사회 전체의 다양한 목소리를 국회에 반영합니다.\\n\\n정리하면, 지역구 의원은 우리 동네 대표, 비례대표 의원은 사회 다양한 목소리를 담는 대표라고 이해하면 돼요.")
  private String content;
}
