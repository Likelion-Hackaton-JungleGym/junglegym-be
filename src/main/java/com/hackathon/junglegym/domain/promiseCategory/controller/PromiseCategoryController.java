package com.hackathon.junglegym.domain.promiseCategory.controller;

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

import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryDeleteRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryUpdateRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.service.PromiseCategoryService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "PromiseCategory", description = "공약 카테고리 관련 API")
public class PromiseCategoryController {

  private final PromiseCategoryService promiseCategoryService;

  @Operation(
      summary = "정치인 공약 카테고리 목록",
      description = "정치인을 선택하면 해당 정치인이 내건 공약 카테고리의 제목/내용 목록을 반환합니다.")
  @GetMapping("/politicians/{politicianId}/promises/categories")
  public ResponseEntity<BaseResponse<List<PromiseCategoryResponse>>> getCategoriesByPolitician(
      @PathVariable Long politicianId) {

    List<PromiseCategoryResponse> response =
        promiseCategoryService.getCategoriesByPolitician(politicianId);

    return ResponseEntity.ok(BaseResponse.success("공약 카테고리 목록 조회 성공", response));
  }

  // 생성
  @Operation(summary = "[개발자] 공약 카테고리 생성", description = "새로운 공약 카테고리 등록 및 생성된 정보 반환 (201 Created)")
  @PostMapping("/dev/politicians/promises/categories")
  public ResponseEntity<BaseResponse<PromiseCategoryResponse>> createPromiseCategory(
      @Parameter(description = "생성할 공약 카테고리 정보") @Valid @RequestBody
          PromiseCategoryRequest request) {
    PromiseCategoryResponse response = promiseCategoryService.createPromiseCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("공약 카테고리 등록 성공", response));
  }

  // 수정
  @Operation(
      summary = "[개발자] 정치인의 특정 공약 카테고리 수정",
      description = "공약 카테고리의 수정할 정보와 새로운 정보를 입력받아 수정합니다.(200 ok)")
  @PatchMapping("/dev/politicians/promises/categories")
  public ResponseEntity<BaseResponse<PromiseCategoryResponse>> updatePromiseCategory(
      @Parameter(description = "수정할 공약 카테고리 정보") @Valid @RequestBody
          PromiseCategoryUpdateRequest request) {
    PromiseCategoryResponse response = promiseCategoryService.updatePromiseCategory(request);
    return ResponseEntity.ok(BaseResponse.success("공약 카테고리 정보 수정", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 정치인의 특정 공약 카테고리 삭제",
      description = "삭제할 공약 카테고리의 정치인과 지역명을 입력받고 해당 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/politicians/promises/categories")
  public ResponseEntity<BaseResponse<String>> deletePromiseCategory(
      @Parameter(description = "삭제할 공약 카테고리 정보") @Valid @RequestBody
          PromiseCategoryDeleteRequest request) {
    promiseCategoryService.deletePromiseCategory(request);
    return ResponseEntity.ok(BaseResponse.success("공약 카테고리 삭제 완료"));
  }
}
