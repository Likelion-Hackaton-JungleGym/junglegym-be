package com.hackathon.junglegym.domain.mediaOrientation.controller;

import com.hackathon.junglegym.domain.mediaOrientation.dto.request.MediaOrientationCreateRequest;
import com.hackathon.junglegym.domain.mediaOrientation.dto.response.MediaOrientationResponse;
import com.hackathon.junglegym.domain.mediaOrientation.service.MediaOrientationService;
import com.hackathon.junglegym.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Media", description = "미디어 관련 api")
public class MediaOrientationController {

  private final MediaOrientationService service;

  @Operation(summary = "미디어 생성", description = "미디어명, 이미지 URL을 입력받아 항목을 생성합니다.")
  @PostMapping("/dev/media")
  public ResponseEntity<BaseResponse<MediaOrientationResponse>> create(
      @Validated @RequestBody MediaOrientationCreateRequest req) {
    MediaOrientationResponse res = service.create(req);
    return ResponseEntity.ok(BaseResponse.success("미디어 생성 성공", res));
  }

  @Operation(summary = "미디어 전체 조회", description = "모든 미디어의 media/imgUrl 목록을 반환합니다.")
  @GetMapping("/dev/media")
  public ResponseEntity<BaseResponse<List<MediaOrientationResponse>>> list() {
    List<MediaOrientationResponse> list = service.list();
    return ResponseEntity.ok(BaseResponse.success("미디어 목록 조회 성공", list));
  }

  @Operation(summary = "미디어 삭제", description = "media(언론사명)로 특정 항목을 삭제합니다.")
  @DeleteMapping("/dev/media/{media}")
  public ResponseEntity<BaseResponse<Void>> delete(@PathVariable String media) {
    service.delete(media);
    return ResponseEntity.ok(BaseResponse.success("미디어 삭제 성공", null));
  }
}
