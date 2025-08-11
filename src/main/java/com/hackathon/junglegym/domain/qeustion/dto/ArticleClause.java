package com.hackathon.junglegym.domain.qeustion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "ArticleClause DTO", description = "PDF 파서가 추출한 조문 단위의 원문 텍스트")
public class ArticleClause {

  @Schema(description = "법령명", example = "공직선거법")
  private String lawName;

  @Schema(description = "법령 유형 - 파일명에서 추출", example = "법률")
  private String lawType;

  @Schema(description = "공포번호 - 파일명에서 추출", example = "제20902호")
  private String promulgationNo;

  @Schema(description = "개정/시행 기준일 - 파일명에서 추출", example = "2025-04-01")
  private String revisionId;

  @Schema(description = "원본 pdf 파일명", example = "공직선거법(법률)(제20902호)(20250401).pdf")
  private String sourceFileName;

  @Schema(description = "pdf 내 시작 페이지 (없으면 -1)", example = "8")
  private Integer pageStart = -1;

  @Schema(description = "pdf 내 종료 페이지 (없으면 -1)", example = "13")
  private Integer pageEnd = -1;

  /* ---------------------- */

  @Schema(description = "장 번호", example = "1")
  private Integer chapter; // 제1장

  @Schema(description = "장 제목", example = "총칙")
  private String chapterTitle; // 제1장 총직

  @Schema(description = "조 번호", example = "1")
  private Integer article; // 제1조

  @Schema(description = "조 제목", example = "목적")
  private String articleTitle; // 제1조 (목적)

  @Schema(description = "조문 원문 텍스트", example = "이 법은 대한민국헌법과 ~~...")
  private String text;
}
