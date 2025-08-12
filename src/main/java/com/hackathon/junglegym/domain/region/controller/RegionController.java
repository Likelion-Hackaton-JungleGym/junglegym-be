package com.hackathon.junglegym.domain.region.controller;

import com.hackathon.junglegym.domain.region.dto.request.RegionRequest;
import com.hackathon.junglegym.domain.region.dto.request.RegionUpdateRequest;
import com.hackathon.junglegym.domain.region.dto.response.RegionResponse;
import com.hackathon.junglegym.domain.region.service.RegionService;
import com.hackathon.junglegym.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/dev/region")
@RequiredArgsConstructor
@Tag(name = "Region", description = "지역 관리 api")
public class RegionController {

  private final RegionService regionService;

  // 생성
  @Operation(summary = "[개발자] 지역 생성", description = "새로운 지역 등록 및 생성된 지역 정보 반환 (201 Created)")
  @PostMapping
  public ResponseEntity<BaseResponse<RegionResponse>> createRegion(
      @Parameter(description = "생성할 지역명", example = "성북구") @Valid @RequestBody
      RegionRequest request) {
    RegionResponse response = regionService.createRegion(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("지역 등록 성공", response));
  }

  // 전체 조회
  @Operation(summary = "[개발자] 지역 목록 조회", description = "모든 지역 정보를 반환합니다. (200 ok)")
  @GetMapping
  public ResponseEntity<BaseResponse<List<RegionResponse>>> getAllRegion() {
    List<RegionResponse> list = regionService.getAllRegion();
    return ResponseEntity.ok(BaseResponse.success("전체 지역 목록 조회 성공", list));
  }

  // 수정
  @Operation(summary = "[개발자] 특정 지역 이름 수정", description = "수정할 지역명과 새로운 지역명을 입력받아 수정합니다.(200 ok)")
  @PatchMapping
  public ResponseEntity<BaseResponse<RegionResponse>> updateRegion(
      @Valid @RequestBody RegionUpdateRequest request) {
    RegionResponse response = regionService.updateRegion(request);
    return ResponseEntity.ok(BaseResponse.success("지역 정보 수정", response));
  }

  // 삭제
  @Operation(summary = "[개발자] 특정 지역 삭제", description = "삭제할 지역이름을 입력받고 해당 지역 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/{name}")
  public ResponseEntity<BaseResponse<String>> deleteRegion(
      @Parameter(description = "삭제할 지역명", example = "성북구") @PathVariable String name) {
    regionService.deleteRegion(name);
    return ResponseEntity.ok(BaseResponse.success("지역 삭제 완료"));
  }
}
