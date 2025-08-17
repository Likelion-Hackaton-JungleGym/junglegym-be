package com.hackathon.junglegym.domain.promise.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.promise.dto.request.PromiseDeleteRequest;
import com.hackathon.junglegym.domain.promise.dto.request.PromiseRequest;
import com.hackathon.junglegym.domain.promise.dto.request.PromiseUpdateRequest;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseProgressSummaryResponse;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.service.PromiseService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

  // 생성
  @Operation(summary = "[개발자] 공약 생성", description = "새로운 공약 등록 및 생성된 정보 반환 (201 Created)")
  @PostMapping("/dev/categories/promises")
  public ResponseEntity<BaseResponse<PromiseResponse>> createPromise(
      @Parameter(description = "생성할 공약 정보") @Valid @RequestBody PromiseRequest request) {
    PromiseResponse response = promiseService.createPromise(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("공약 등록 성공", response));
  }

  // 수정
  @Operation(
      summary = "[개발자] 정치인의 특정 공약 수정",
      description = "공약의 수정할 정보와 새로운 정보를 입력받아 수정합니다.(200 ok)")
  @PatchMapping("/dev/categories/promises")
  public ResponseEntity<BaseResponse<PromiseResponse>> updatePromise(
      @Parameter(description = "수정할 공약 정보") @Valid @RequestBody PromiseUpdateRequest request) {
    PromiseResponse response = promiseService.updatePromise(request);
    return ResponseEntity.ok(BaseResponse.success("공약 정보 수정", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 정치인의 특정 공약 삭제",
      description = "삭제할 공약의 정치인과 지역명을 입력받고 해당 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/categories/promises")
  public ResponseEntity<BaseResponse<String>> deletePromise(
      @Parameter(description = "삭제할 공약 정보") @Valid @RequestBody PromiseDeleteRequest request) {
    promiseService.deletePromise(request);
    return ResponseEntity.ok(BaseResponse.success("공약 삭제 완료"));
  }
}
