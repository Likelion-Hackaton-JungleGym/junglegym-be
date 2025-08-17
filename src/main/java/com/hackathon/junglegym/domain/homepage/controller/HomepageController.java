package com.hackathon.junglegym.domain.homepage.controller;

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

import com.hackathon.junglegym.domain.homepage.dto.request.HomepageDeleteRequest;
import com.hackathon.junglegym.domain.homepage.dto.request.HomepageRequest;
import com.hackathon.junglegym.domain.homepage.dto.response.HomepageResponse;
import com.hackathon.junglegym.domain.homepage.service.HomepageService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Homepage", description = "홈페이지 관리 api")
public class HomepageController {

  private final HomepageService homepageService;

  // 생성
  @Operation(summary = "[개발자] 홈페이지 생성", description = "새로운 홈페이지 등록 및 생성된 홈페이지 정보 반환 (201 Created)")
  @PostMapping("/dev/homepage")
  public ResponseEntity<BaseResponse<HomepageResponse>> createHomepage(
      @Parameter(description = "생성할 홈페이지 정보") @Valid @RequestBody HomepageRequest request) {
    HomepageResponse response = homepageService.createHomepage(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("홈페이지 등록 성공", response));
  }

  // 전체 조회
  @Operation(summary = "[개발자] 정치인의 홈페이지 목록 조회", description = "특정 정치인의 모든 홈페이지 정보를 반환합니다. (200 ok)")
  @GetMapping("/politician/{politicianId}/homepages")
  public ResponseEntity<BaseResponse<List<HomepageResponse>>> getAllHomepage(
      @Parameter(description = "조회할 정치인 고유번호", example = "1") @PathVariable Long politicianId) {
    List<HomepageResponse> list = homepageService.getAllHomepage(politicianId);
    return ResponseEntity.ok(BaseResponse.success("전체 홈페이지 목록 조회 성공", list));
  }

  // 수정
  @Operation(
      summary = "[개발자] 정치인의 특정 홈페이지 수정",
      description = "수정할 홈페이지 정보와 새로운 홈페이지 정보를 입력받아 수정합니다.(200 ok)")
  @PatchMapping("/dev/homepage")
  public ResponseEntity<BaseResponse<HomepageResponse>> updateHomepage(
      @Parameter(description = "수정할 홈페이지 정보") @Valid @RequestBody HomepageRequest request) {
    HomepageResponse response = homepageService.updateHomepage(request);
    return ResponseEntity.ok(BaseResponse.success("홈페이지 정보 수정", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 정치인의 특정 홈페이지 삭제",
      description = "삭제할 홈페이지의 정치인과 지역명을 입력받고 해당 홈페이지 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/homepage")
  public ResponseEntity<BaseResponse<String>> deleteHomepage(
      @Parameter(description = "삭제할 홈페이지 정보") @Valid @RequestBody HomepageDeleteRequest request) {
    homepageService.deleteHomepage(request);
    return ResponseEntity.ok(BaseResponse.success("홈페이지 삭제 완료"));
  }
}
