package com.hackathon.junglegym.domain.promiseCategory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.service.PromiseCategoryService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
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
  @GetMapping("/{politicianId}/promises/categories")
  public ResponseEntity<BaseResponse<List<PromiseCategoryResponse>>> getCategoriesByPolitician(
      @PathVariable Long politicianId) {

    List<PromiseCategoryResponse> response =
        promiseCategoryService.getCategoriesByPolitician(politicianId);

    return ResponseEntity.ok(BaseResponse.success("공약 카테고리 목록 조회 성공", response));
  }
}
