package com.hackathon.junglegym.domain.criminalRecord.controller;

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

import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordDeleteRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordUpdateRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.response.CriminalRecordResponse;
import com.hackathon.junglegym.domain.criminalRecord.service.CriminalRecordService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "CriminalRecord", description = "전과 관리 api")
public class CriminalRecordController {

  private final CriminalRecordService service;

  // 생성
  @Operation(summary = "[개발자] 전과 생성", description = "새로운 전과 등록 및 생성된 전과 정보 반환 (201 Created)")
  @PostMapping("/dev/criminalRecord")
  public ResponseEntity<BaseResponse<CriminalRecordResponse>> createHomepage(
      @Parameter(description = "생성할 전과 정보") @Valid @RequestBody CriminalRecordRequest request) {
    CriminalRecordResponse response = service.createCriminalRecord(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("전과 등록 성공", response));
  }

  // 전체 조회
  @Operation(summary = "[개발자] 정치인의 전과 목록 조회", description = "특정 정치인의 모든 전과 정보를 반환합니다. (200 ok)")
  @GetMapping("/politician/{politicianId}/criminalRecord")
  public ResponseEntity<BaseResponse<List<CriminalRecordResponse>>> getAllHomepage(
      @Parameter(description = "조회할 정치인 고유번호", example = "1") @PathVariable Long politicianId) {
    List<CriminalRecordResponse> list = service.getAllCriminalRecord(politicianId);
    return ResponseEntity.ok(BaseResponse.success("전체 전과 목록 조회 성공", list));
  }

  // 수정
  @Operation(
      summary = "[개발자] 정치인의 특정 전과 수정",
      description = "수정할 전과 정보와 새로운 전과 정보를 입력받아 수정합니다.(200 ok)")
  @PatchMapping("/dev/criminalRecord")
  public ResponseEntity<BaseResponse<CriminalRecordResponse>> updateHomepage(
      @Parameter(description = "수정할 전과 정보") @Valid @RequestBody
          CriminalRecordUpdateRequest request) {
    CriminalRecordResponse response = service.updateCriminalRecord(request);
    return ResponseEntity.ok(BaseResponse.success("전과 정보 수정", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 정치인의 특정 전과 삭제",
      description = "삭제할 전과의 정치인과 지역명을 입력받고 해당 전과 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/criminalRecord")
  public ResponseEntity<BaseResponse<String>> deleteHomepage(
      @Parameter(description = "삭제할 전과 정보") @Valid @RequestBody
          CriminalRecordDeleteRequest request) {
    service.deleteCriminalRecord(request);
    return ResponseEntity.ok(BaseResponse.success("전과 삭제 완료"));
  }
}
