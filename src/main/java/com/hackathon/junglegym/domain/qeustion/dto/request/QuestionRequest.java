package com.hackathon.junglegym.domain.qeustion.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "QuestionRequest DTO", description = "챗봇 질문을 위한 데이터 전송")
public class QuestionRequest {

  @NotBlank(message = "질문은 비어 있을 수 없습니다.")
  @Schema(description = "사용자 질문", example = "서울 시장은 특정 당에 소속되어 있지 않는거야?")
  private String question;

  @Schema(description = "비공개 여부(false면 DB에 저장)", example = "false")
  private boolean isPrivated;
}
