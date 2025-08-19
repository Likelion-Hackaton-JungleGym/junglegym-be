package com.hackathon.junglegym.domain.newsletter.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "NewsletterResponse DTO", description = "뉴스레터 응답 DTO")
public class NewsletterResponse {

  @Schema(description = "뉴스레터 ID", example = "1")
  private Long id;

  @Schema(description = "지역명", example = "성북구")
  private String regionName;

  @Schema(description = "언론사명", example = "중앙일보")
  private String media;

  @Schema(description = "언론사 성향 이미지 URL", example = "https://.../img.png")
  private String mediaImgUrl;

  @Schema(description = "뉴스레터 제목", example = "청년 월세 지원 정책, 성북에선 왜 인기일까?")
  private String title;

  @Schema(description = "노출 날짜", example = "2025-08-19")
  private LocalDate date;

  @Schema(description = "링크", example = "https://news.example.com/article")
  private String link;

  @Schema(description = "썸네일 URL", example = "https://server.com/thumbnail.png")
  private String thumbnailUrl;

  @Schema(description = "인트로 제목", example = "정글의 소리 - 오늘의 질문!")
  private String inTitle;

  @Schema(description = "소제목1", example = "청년 월세 지원 정책이란?")
  private String subtitle1;

  @Schema(description = "내용1", example = "## Markdown 내용")
  private String content1;

  @Schema(description = "소제목2", example = "정책 반응 비교")
  private String subtitle2;

  @Schema(description = "내용2", example = "## Markdown 내용")
  private String content2;

  @Schema(description = "오늘의 질문 제목", example = "오늘의 질문")
  private String todayQuestion;

  @Schema(description = "질문 타이틀", example = "똑같은 정책인데, 어떤 동네는 뜨겁고 어떤 동네는 외면?")
  private String titleQuestion;

  @Schema(description = "질문 내용 (Markdown)", example = "**왜 차이가 나는 걸까요?**")
  private String questionContent;
}
