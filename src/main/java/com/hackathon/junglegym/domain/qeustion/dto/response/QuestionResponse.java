package com.hackathon.junglegym.domain.qeustion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "QuestionResponse DTO", description = "챗봇 질문에 대한 응답(답변)")
public class QuestionResponse {

  @Schema(description = "질문", example = "서울 시장은 특정 당에 소속되어 있지 않는거야?")
  private String question;

  @Schema(description = "답변", example = "서울시장처럼 지방자치단체장은 ~~...")
  private String answer;

  @Schema(description = "관련 법 조항", example = "헌법 제 116조 제1항 ~~...")
  private String constitution;
}
