package com.hackathon.junglegym.domain.bill.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(title = "BillResponse: 발의법률 응답 DTO")
public class BillResponse {

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(title = "BillListItemResponse: 발의법률 단건 응답 DTO")
  public static class BillListItemResponse {

    @Schema(description = "법률안 ID", example = "2001")
    private Long billId;

    @Schema(description = "법률안 명", example = "항공안전법 일부개정법률안")
    private String name;

    @Schema(
        description = "법률안 제안이유 및 주요내용",
        example =
            "공항운영자가 공항시설 보호구역에 출입을 허가하는 경우 출입이 가능한 보호구역의 범위를 정하여 허가하도록 하고, 민간항공의 보안을 해치거나 해칠 우려가 있는 사실을 발생시킨 자가 항공보안 자율신고를 하는 경우 고의 또는 중대한 과실이 없으면 이 법에 따른 처분을 할 수 없도록 하며, 항공보안 감독관이 수행하는 보안사고 등에 대한 조사의 명시적인 근거를 마련하는 등 현행 제도의 운영상 나타난 일부 미비점을 개선ㆍ보완하려는 것임.")
    private String mainContent;

    @Schema(
        description = "법률안 제안이유 및 주요내용 요약된 내용",
        example =
            "\t1.\t공항운영자는 보호구역 출입 허가 시 출입 범위를 정해 허가함.\n"
                + "\t2.\t보안을 해칠 우려가 있는 자가 자율신고 시, 고의·중과실 없으면 처분 불가.\n"
                + "\t3.\t항공보안 감독관의 보안사고 조사 근거를 마련함.\n"
                + "\t4.\t현행 제도의 미비점을 개선·보완하려는 것임.")
    private String summaryContent;

    @Schema(description = "본회의심의결과", example = "대안반영폐기")
    private String result;

    @Schema(description = "발의일", example = "2025-08-01")
    private LocalDate proposeDate;

    @Schema(description = "대표발의자", example = "김영배")
    private String mainProposer;

    @Schema(description = "공동발의자", example = "이철수, 박영희, ...")
    private String joinProposer;

    @Schema(description = "상세 링크", example = "https://open.assembly.go.kr/bill/2001")
    private String detailLink;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(title = "PagedBillPageResponse: 발의법률 페이지 응답 DTO (10개 고정)")
  public static class PagedBillPageResponse {

    @Schema(description = "현재 페이지(1-base)", example = "4")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "총 페이지 수", example = "131")
    private int totalPages;

    @Schema(description = "총 건수", example = "1301")
    private long totalElements;

    @Schema(description = "이전 페이지 존재", example = "true")
    private boolean hasPrev;

    @Schema(description = "다음 페이지 존재", example = "true")
    private boolean hasNext;

    @Schema(description = "현재 페이지 목록")
    private List<BillListItemResponse> items;
  }
}
