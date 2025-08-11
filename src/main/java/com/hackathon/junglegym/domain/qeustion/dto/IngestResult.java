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
@Schema(title = "IngestResult DTO", description = "PDF Ingest 처리 결과")
public class IngestResult {

  @Schema(description = "청크 id", example = "공직선거법-1-2-0")
  private String id;

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

  @Schema(description = "대상 Qdrant 컬렉션명", example = "law_chunks")
  private String collection;

  @Schema(description = "파싱된 조문 수(조 단위 블록 수)", example = "352")
  private Integer totalArticles;

  @Schema(description = "생성된 청크 수", example = "1034")
  private Integer totalChunks;

  @Schema(description = "Qdrant 업서트 성공 개수", example = "1034")
  private Integer success;

  @Schema(description = "Qdrant 업서트 실패 개수", example = "0")
  private Integer failed;

  @Schema(description = "처리 소요 시간(ms)", example = "18432")
  private Long elapsedMs;

  @Schema(description = "경고/노이즈 요약(머리말 제거 등)", example = "머리말 38곳 제거")
  private String warnings;
}
