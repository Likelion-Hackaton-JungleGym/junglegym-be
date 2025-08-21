package com.hackathon.junglegym.domain.regionNews.dto.response;

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
@Schema(title = "RegionNewsResponse DTO", description = "지역뉴스 응답 DTO")
public class RegionNewsResponse {

  @Schema(description = "지역뉴스 ID", example = "1")
  private Long id;

  @Schema(description = "뉴스카테고리", example = "성북구")
  private String newsCategory;

  @Schema(description = "지역뉴스 제목", example = "성북, 자치회관 프로그램 온라인 접수")
  private String title;

  @Schema(description = "지역뉴스 한줄 요약", example = "누구나 클릭 한 번으로 신청 가능")
  private String oneLineContent;

  @Schema(
      description = "내용 요약",
      example =
          "성북구가 자치회관 프로그램 신청 방식을 개선해 온라인 접수 시스템을 시범 운영 중입니다. PC나 스마트폰으로 언제든 프로그램을 확인하고 신청할 수 있습니다. 노년층을 고려해 단계적으로 도입하며, 9월부터 일부 프로그램에 우선 적용하고 2026년에는 전면 확대할 계획입니다.")
  private String summary;

  @Schema(description = "언론사명", example = "국회일보")
  private String media;

  @Schema(description = "언론사 성향 이미지 URL", example = "https://.../img.png")
  private String mediaImgUrl;

  @Schema(description = "출간 날짜", example = "2025-08-19")
  private LocalDate date;

  @Schema(description = "기사 링크", example = "https://news.example.com/article")
  private String link;
}
