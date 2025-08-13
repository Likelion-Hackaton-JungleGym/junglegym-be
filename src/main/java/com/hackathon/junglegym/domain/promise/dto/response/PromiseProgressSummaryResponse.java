package com.hackathon.junglegym.domain.promise.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PromiseProgressSummaryResponse: 공약 사업 이행 현황 요약 응답")
public class PromiseProgressSummaryResponse {

  // 총계
  @Schema(description = "총 사업 수", example = "77")
  private int totalCount;

  // 완료 계열
  @Schema(description = "완료 공약 수", example = "22")
  private int doneCount;

  @Schema(description = "이행 후 계속 추진 공약 수", example = "25")
  private int ongoingCount;

  // 추진 중
  @Schema(description = "정상 추진 공약 수", example = "30")
  private int inProgressNormalCount;

  @Schema(description = "일부 추진 공약 수", example = "0")
  private Integer inProgressPartialCount; // enum에 PARTIAL이 없으면 0으로 집계

  // 미이행
  @Schema(description = "보류 공약 수", example = "0")
  private Integer onHoldCount;

  @Schema(description = "폐기 공약 수", example = "0")
  private Integer cancelledCount;

  // 소계
  @Schema(description = "완료 소계(완료 + 이행 후 계속 추진)", example = "47")
  private Integer completedSubtotal;

  @Schema(description = "추진 중 소계(정상 + 일부 추진)", example = "30")
  private Integer inProgressSubtotal;

  @Schema(description = "미이행 소계(보류 + 폐기)", example = "0")
  private Integer notImplementedSubtotal;

  // 각 비율(0.0 ~ 100.0)
  @Schema(description = "완료 비율(%)", example = "28")
  private Integer doneRate;

  @Schema(description = "이행 후 계속 추진 비율(%)", example = "32")
  private Integer ongoingRate;

  @Schema(description = "정상 추진 비율(%)", example = "38")
  private Integer inProgressNormalRate;

  @Schema(description = "일부 추진 비율(%)", example = "0")
  private Integer inProgressPartialRate;

  @Schema(description = "보류 비율(%)", example = "0")
  private Integer onHoldRate;

  @Schema(description = "폐기 비율(%)", example = "0")
  private Integer cancelledRate;

  @Schema(description = "완료 소계 비율(%)", example = "61")
  private Integer completedSubtotalRate;

  @Schema(description = "추진 중 소계 비율(%)", example = "38")
  private Integer inProgressSubtotalRate;

  @Schema(description = "미이행 소계 비율(%)", example = "0")
  private Integer notImplementedSubtotalRate;

  // 데이터 최신화 일자 (예: 가장 최근 갱신일의 날짜부)
  @Schema(description = "업데이트 기준일", example = "2025-08-08")
  private LocalDate updatedDate;
}
