package com.hackathon.junglegym.domain.newsletter.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.newsletter.dto.request.NewsletterCreateRequest;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterListItemResponse;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterResponse;
import com.hackathon.junglegym.domain.newsletter.service.NewsletterService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "정글의 소리 (뉴스레터) 관련 api")
public class NewsletterController {

  private final NewsletterService newsletterService;

  @Operation(
      summary = "[개발자] 뉴스레터 생성",
      description = "지역명과 언론사명으로 연관관계를 매핑하여 뉴스레터를 생성합니다. (201 Created)")
  @PostMapping("/dev/newsletters")
  public ResponseEntity<BaseResponse<NewsletterResponse>> createNewsletter(
      @Parameter(description = "생성할 뉴스레터 정보") @Valid @RequestBody NewsletterCreateRequest request) {

    NewsletterResponse response = newsletterService.createNewsletter(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("뉴스레터 등록 성공", response));
  }

  @Operation(
      summary = "특정 지역 뉴스레터 목록 조회",
      description = "지역명을 쿼리스트링으로 전달하면 해당 지역(+서울시)의 뉴스레터 목록을 최신순으로 반환합니다. (200 OK)")
  @GetMapping("/regions/newsletters")
  public ResponseEntity<BaseResponse<List<NewsletterListItemResponse>>> getNewslettersByRegion(
      @Parameter(description = "지역명", example = "성북구")
          @RequestParam(value = "regionName", defaultValue = "성북구")
          String regionName) {

    List<NewsletterListItemResponse> list = newsletterService.getNewslettersByRegion(regionName);
    return ResponseEntity.ok(BaseResponse.success("특정 지역 뉴스레터 목록 조회 성공", list));
  }

  @Operation(summary = "뉴스레터 단건 조회", description = "ID로 특정 뉴스레터를 조회합니다. (200 OK)")
  @GetMapping("/newsletters/{newsletterId}")
  public ResponseEntity<BaseResponse<NewsletterResponse>> getNewsletterById(
      @Parameter(description = "조회할 뉴스레터 ID") @PathVariable Long newsletterId) {
    NewsletterResponse response = newsletterService.getNewsletterById(newsletterId);
    return ResponseEntity.ok(BaseResponse.success("뉴스레터 단건 조회 성공", response));
  }

  @Operation(summary = "[개발자] 뉴스레터 수정", description = "ID로 뉴스레터를 수정합니다. (200 OK)")
  @PutMapping("/dev/newsletters/{newsletterId}")
  public ResponseEntity<BaseResponse<NewsletterResponse>> updateNewsletter(
      @Parameter(description = "수정할 뉴스레터 ID") @PathVariable Long newsletterId,
      @Parameter(description = "수정 내용") @Valid @RequestBody NewsletterCreateRequest request) {

    NewsletterResponse response = newsletterService.updateNewsletter(newsletterId, request);
    return ResponseEntity.ok(BaseResponse.success("뉴스레터 수정 성공", response));
  }

  @Operation(summary = "[개발자] 뉴스레터 삭제", description = "ID로 뉴스레터를 삭제합니다. (200 OK)")
  @DeleteMapping("/dev/newsletters/{newsletterId}")
  public ResponseEntity<BaseResponse<Long>> deleteNewsletter(
      @Parameter(description = "삭제할 뉴스레터 ID") @PathVariable Long newsletterId) {

    newsletterService.deleteNewsletter(newsletterId);
    return ResponseEntity.ok(BaseResponse.success("뉴스레터 삭제 성공", newsletterId));
  }
}
