package com.hackathon.junglegym.domain.dictionary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.dictionary.dto.request.DictionaryCreateRequest;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryDetailResponse;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryListItemResponse;
import com.hackathon.junglegym.domain.dictionary.entity.Dictionary;
import com.hackathon.junglegym.domain.dictionary.service.DictionaryService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Dictionary", description = "정글 사전 관련 api")
public class DictionaryController {

  private final DictionaryService dictionaryService;

  @Operation(summary = "정글 사전 생성", description = "키워드, 제목, 부제목, 내용을 입력받아 사전 항목을 생성합니다.")
  @PostMapping("/dev/dictionaries")
  public ResponseEntity<BaseResponse<Dictionary>> createDictionary(
      @RequestBody DictionaryCreateRequest request) {

    Dictionary dictionary = dictionaryService.createDictionary(request);
    return ResponseEntity.ok(BaseResponse.success("정글 사전 생성 성공", dictionary));
  }

  @Operation(summary = "정글 사전 전체 조회(요약)", description = "최근 생성 순서대로 키워드/제목/부제목만 반환합니다.")
  @GetMapping("/dictionaries")
  public ResponseEntity<BaseResponse<List<DictionaryListItemResponse>>> getAll() {
    return ResponseEntity.ok(BaseResponse.success("정글 사전 목록 조회 성공", dictionaryService.getAll()));
  }

  @Operation(summary = "정글 사전 상세 조회", description = "키워드/제목/부제목/내용을 반환합니다.")
  @GetMapping("/dictionaries/{dictionaryId}")
  public ResponseEntity<BaseResponse<DictionaryDetailResponse>> getById(
      @PathVariable Long dictionaryId) {
    return ResponseEntity.ok(
        BaseResponse.success("정글 사전 상세 조회 성공", dictionaryService.getById(dictionaryId)));
  }

  @Operation(summary = "정글 사전 수정", description = "ID로 특정 사전 항목을 수정합니다.")
  @PutMapping("/dev/dictionaries/{dictionaryId}")
  public ResponseEntity<BaseResponse<Dictionary>> updateDictionary(
      @PathVariable Long dictionaryId, @RequestBody DictionaryCreateRequest request) {
    Dictionary updated = dictionaryService.updateDictionary(dictionaryId, request);
    return ResponseEntity.ok(BaseResponse.success("정글 사전 수정 성공", updated));
  }

  @Operation(summary = "정글 사전 삭제", description = "ID로 특정 사전 항목을 삭제합니다.")
  @DeleteMapping("/dev/dictionaries/{dictionaryId}")
  public ResponseEntity<BaseResponse<Void>> deleteDictionary(@PathVariable Long dictionaryId) {
    dictionaryService.deleteDictionary(dictionaryId);
    return ResponseEntity.ok(BaseResponse.success("정글 사전 삭제 성공", null));
  }
}
