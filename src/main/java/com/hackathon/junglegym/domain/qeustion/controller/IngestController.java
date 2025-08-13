package com.hackathon.junglegym.domain.qeustion.controller;

import com.hackathon.junglegym.domain.qeustion.dto.IngestResult;
import com.hackathon.junglegym.domain.qeustion.service.IngestService;
import com.hackathon.junglegym.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dev/ingest")
@RequiredArgsConstructor
@Tag(name = "Ingest", description = "PDF 업로드 인제스트용 api")
public class IngestController {

  private final IngestService ingestService;

  @Operation(
      summary = "[개발자] PDF 인제스트",
      description = "여러 파일을 받아 추출, 파싱, 청킹, 임베딩, 업서트를 진행합니다. (201 Created)")
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<List<IngestResult>>> ingestFile(
      @Parameter(
          description = "PDF 파일들",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      @RequestPart("files")
      List<MultipartFile> files) throws IOException {

    List<IngestResult> results = new ArrayList<>();
    for (MultipartFile file : files) {
      results.add(ingestService.ingestPdfAuto(file));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("PDF ingest 완료", results));
  }
}
