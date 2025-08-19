package com.hackathon.junglegym.domain.newsletter.dto.request;

import java.time.LocalDate;

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
@Schema(title = "NewsletterCreateRequest DTO", description = "뉴스레터 생성 요청")
public class NewsletterCreateRequest {

  @NotBlank
  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @NotBlank
  @Schema(description = "언론사명(PK)", example = "경향신문")
  private String media; // media_orientation.media (PK)

  @Schema(description = "상단 큰 제목", example = "\uD83D\uDCB3 성북구 소비쿠폰, 벌써 96%가 받아갔다고?! \uD83C\uDF89")
  private String title;

  @Schema(description = "노출 날짜", example = "2025-08-14")
  private LocalDate date;

  @Schema(description = "외부 링크(유튜브/기사)", example = "https://www.khan.co.kr/article/202508141624001")
  private String link;

  @Schema(
      description = "썸네일 URL (기사일 때 서버 저장 경로)",
      example = "https://cdn.mysvc.com/news/2025/08/thumb_123.jpg")
  private String thumbnailUrl;

  @Schema(description = "본문 큰 소제목", example = "청년 월세 지원 정책, 성북에선 왜 이렇게 인기일까?")
  private String inTitle;

  @Schema(description = "섹션1 소제목", example = "청년 월세 지원 정책이란?")
  private String subtitle1;

  @Schema(description = "섹션1 본문(Markdown)")
  private String content1;

  @Schema(description = "섹션2 소제목", example = "왜 성북은 다르게 반응했을까?")
  private String subtitle2;

  @Schema(description = "섹션2 본문(Markdown)")
  private String content2;

  @Schema(description = "오늘의 질문 제목", example = "\uD83C\uDFA4 정글의 소리 – 오늘의 질문!")
  private String todayQuestion;

  @Schema(description = "질문 타이틀", example = "\uD83D\uDCAD 쿠폰이 더 좋은 걸까, 현금이 더 좋은 걸까?")
  private String titleQuestion;

  @Schema(description = "질문 상세(Markdown)")
  private String questionContent;
}
