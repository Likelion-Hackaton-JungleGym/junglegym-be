package com.hackathon.junglegym.domain.politician.controller;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianRequest;
import com.hackathon.junglegym.domain.politician.dto.request.PoliticianUpdateRequest;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianByRegionResponse;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianResponse;
import com.hackathon.junglegym.domain.politician.service.PoliticianService;
import com.hackathon.junglegym.global.response.BaseResponse;
import com.hackathon.junglegym.global.s3.dto.S3Response;
import com.hackathon.junglegym.global.s3.entity.PathName;
import com.hackathon.junglegym.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Politician", description = "정치인 관리 api")
public class PoliticianController {

  private final PoliticianService politicianService;
  private final S3Service s3Service;

  // 생성
  @Operation(summary = "[개발자] 정치인 생성", description = "새로운 정치인 등록 및 생성된 정치인 정보 반환 (201 Created)")
  @PostMapping("/dev/politician")
  public ResponseEntity<BaseResponse<PoliticianResponse>> createPolitician(
      @Parameter(description = "생성할 정치인 정보") @Valid @RequestBody PoliticianRequest request) {
    PoliticianResponse response = politicianService.createPolitician(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("정치인 등록 성공", response));
  }

  // 전체 조회
  @Operation(summary = "[개발자] 정치인 목록 조회", description = "데이터베이스에 등록된 모든 정치인들을 조회합니다. (200 ok)")
  @GetMapping("/dev/politician")
  public ResponseEntity<BaseResponse<List<PoliticianResponse>>> getAllPolitician() {
    List<PoliticianResponse> list = politicianService.getAllPolitician();
    return ResponseEntity.ok(BaseResponse.success("전체 정치인 목록 조회 성공", list));
  }

  // 특정 지역 정치인들 조회
  @Operation(summary = "특정 지역 정치인 목록 조회", description = "특정 지역의 정치인들을 조회합니다. (200 ok)")
  @GetMapping("/regions/politicians")
  public ResponseEntity<BaseResponse<List<PoliticianByRegionResponse>>> getAllPoliticianByRegion(
      @Parameter(description = "지역명", example = "성북구")
      @RequestParam(value = "regionName", defaultValue = "성북구")
      String regionName) {
    List<PoliticianByRegionResponse> list = politicianService.getAllPoliticianByRegion(regionName);
    return ResponseEntity.ok(BaseResponse.success("특정 지역 정치인 목록 조회 성공", list));
  }

  // 단일 조회
  @Operation(summary = "특정 정치인 조회", description = "특정 정치인의 세부정보를 조회합니다 (200 ok)")
  @GetMapping("/politician/{politicianId}")
  public ResponseEntity<BaseResponse<PoliticianResponse>> getPoliticianById(
      @Parameter(description = "조회할 정치인 고유번호", example = "1") @PathVariable Long politicianId) {
    PoliticianResponse response = politicianService.getPoliticianById(politicianId);
    return ResponseEntity.ok(BaseResponse.success("특정 정치인 조회 성공", response));
  }

  // 수정
  @Operation(
      summary = "[개발자] 특정 정치인 데이터 수정",
      description = "이름과 지역명을 입력받고, 해당 정치인의 데이터를 수정합니다. (200 ok)")
  @PatchMapping("/dev/politician")
  public ResponseEntity<BaseResponse<PoliticianResponse>> updatePolitician(
      @Valid @RequestBody PoliticianUpdateRequest request) {

    PoliticianResponse response = politicianService.updatePolitician(request);
    return ResponseEntity.ok(BaseResponse.success("정치인 수정 완료", response));
  }

  // 삭제
  @Operation(
      summary = "[개발자] 특정 정치인 삭제",
      description = "이름과 지역명을 입력받고, 해당 정치인 데이터를 삭제합니다. (200 ok)")
  @DeleteMapping("/dev/politician")
  public ResponseEntity<BaseResponse<String>> deletePolitician(
      @Parameter(description = "삭제할 정치인 이름", example = "김영배") @Valid @RequestBody String name,
      @Parameter(description = "지역명", example = "성북구") @Valid @RequestBody String regionName) {
    politicianService.deletePolitician(name, regionName);
    return ResponseEntity.ok(BaseResponse.success("정치인 삭제 완료"));
  }

  // 정치인 사진 등록
  @Operation(
      summary = "[개발자] 정치인 이미지 파일 등록",
      description = "이름과 지역으로 검색한 정치인의 이미지 사진을 등록합니다. (201 Created)")
  @PostMapping(value = "/dev/politician/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<PoliticianResponse>> createPoliticianImg(
      @Parameter(description = "정치인 이름", example = "김영배") @Valid @RequestParam("name") String name,
      @Parameter(description = "정치인 지역명", example = "성북구") @Valid @RequestParam("regionName")
      String regionName,
      @Parameter(
          description = "정치인 사진",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @Valid
      @RequestPart("file")
      MultipartFile file) {
    S3Response imgUrl = s3Service.uploadImage(PathName.POLITICIAN, file);
    PoliticianResponse response = politicianService.createPoliticianImg(name, regionName, imgUrl);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("정치인 이미지파일 등록 성공", response));
  }
}
