package com.hackathon.junglegym.domain.promise.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.promise.dto.response.PromiseProgressSummaryResponse;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.service.PromiseService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Promise", description = "공약 관련 API")
public class PromiseController {

  private final PromiseService promiseService;

  @Operation(summary = "정치인 공약 이행 현황 요약", description = "총계, 상태별 개수/비율, 소계, 업데이트 기준일을 반환합니다.")
  @GetMapping("/politicians/{politicianId}/promises/summary")
  public ResponseEntity<BaseResponse<PromiseProgressSummaryResponse>> getSummaryByPolitician(
      @PathVariable Long politicianId) {
    PromiseProgressSummaryResponse response =
        promiseService.getProgressSummaryByPolitician(politicianId);
    return ResponseEntity.ok(BaseResponse.success("공약 이행 현황 요약 조회 성공", response));
  }

  @Operation(summary = "공약 카테고리 상세 공약 목록", description = "카테고리를 선택하면 공약명/진행상황/공약ID/공약목표를 반환합니다.")
  @GetMapping("/categories/{categoryId}/promises")
  public ResponseEntity<BaseResponse<List<PromiseResponse>>> getPromisesByCategory(
      @PathVariable Long categoryId) {

    List<PromiseResponse> response = promiseService.getPromisesByCategory(categoryId);
    return ResponseEntity.ok(BaseResponse.success("공약 목록 조회 성공", response));
  }
}
