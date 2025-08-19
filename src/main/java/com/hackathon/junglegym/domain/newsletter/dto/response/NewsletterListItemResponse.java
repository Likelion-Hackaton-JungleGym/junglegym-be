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
@Schema(title = "NewsletterListItemResponse: 뉴스레터 목록 단건")
public class NewsletterListItemResponse {

  @Schema(description = "뉴스레터 ID", example = "7")
  private Long newsletterId;

  @Schema(description = "제목", example = "청년 월세 지원 정책, 강남은 외면·성북은 인기?")
  private String title;

  @Schema(description = "날짜", example = "2025-08-02")
  private LocalDate date;

  @Schema(description = "요약(첫 섹션) - Markdown", example = "서울시가 작년에 시작한 청년 월세 지원 정책…")
  private String content1;

  @Schema(description = "외부 링크(유튜브/기사)", example = "https://news.example.com/a/123")
  private String link;

  @Schema(description = "썸네일 이미지 URL (기사일 때만 세팅)", example = "https://cdn.../thumb.jpg")
  private String thumbnailImg; // = entity.thumbnailUrl
}
