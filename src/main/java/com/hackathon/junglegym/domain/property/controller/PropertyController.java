package com.hackathon.junglegym.domain.property.controller;

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

import com.hackathon.junglegym.domain.property.dto.request.PropertyRequest;
import com.hackathon.junglegym.domain.property.dto.response.PropertyResponse;
import com.hackathon.junglegym.domain.property.service.PropertyService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Property", description = "재산 관리 api")
public class PropertyController {

  private final PropertyService propertyService;

  // 생성
  @Operation(summary = "[개발자] 재산 생성", description = "새로운 재산 등록 및 생성된 재산 정보 반환 (201 Created)")
  @PostMapping("/dev/property")
  public ResponseEntity<BaseResponse<PropertyResponse>> createProperty(
      @Parameter(description = "생성할 재산 정보") @Valid @RequestBody PropertyRequest request) {
    PropertyResponse response = propertyService.createProperty(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("재산 등록 성공", response));
  }

  // 조회
  @Operation(summary = "[개발자] 정치인의 재산 목록 조회", description = "특정 정치인의 모든 재산 정보를 반환합니다. (200 ok)")
  @GetMapping("/politician/{politicianId}/property")
  public ResponseEntity<BaseResponse<PropertyResponse>> getAllProperty(
      @Parameter(description = "조회할 정치인 고유번호", example = "1") @PathVariable Long politicianId) {
    PropertyResponse response = propertyService.getAllProperty(politicianId);
    return ResponseEntity.ok(BaseResponse.success("재산 조회 성공", response));
  }

  // 수정
  @Operation(
      summary = "[개발자] 정치인의 특정 재산 수정",
      description = "수정할 재산 정보와 새로운 재산 정보를 입력받아 수정합니다.(200 ok)")
  @PatchMapping("/dev/property")
  public ResponseEntity<BaseResponse<PropertyResponse>> updateProperty(
      @Parameter(description = "수정할 재산 정보") @Valid @RequestBody PropertyRequest request) {
    PropertyResponse response = propertyService.updateProperty(request);
    return ResponseEntity.ok(BaseResponse.success("재산 정보 수정", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 정치인의 특정 재산 삭제",
      description = "삭제할 재산의 정치인과 지역명을 입력받고 해당 재산 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/property")
  public ResponseEntity<BaseResponse<String>> deleteProperty(
      @Parameter(description = "삭제할 정치인 이름", example = "김영배") @Valid @RequestBody String name,
      @Parameter(description = "지역명", example = "성북구") @Valid @RequestBody String regionName) {
    propertyService.deleteProperty(regionName, name);
    return ResponseEntity.ok(BaseResponse.success("재산 삭제 완료"));
  }
}
