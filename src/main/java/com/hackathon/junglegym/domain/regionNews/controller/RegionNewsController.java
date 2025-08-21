package com.hackathon.junglegym.domain.regionNews.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.regionNews.dto.response.RegionNewsResponse;
import com.hackathon.junglegym.domain.regionNews.service.RegionNewsService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "RegionNews", description = "지역 주간 이슈")
public class RegionNewsController {

  private final RegionNewsService regionNewsService;

  /** 특정 지역 지난주 이슈 5건 */
  @GetMapping("/dev/{regionId}/weekly")
  public ResponseEntity<BaseResponse<List<RegionNewsResponse>>> getWeekly(
      @PathVariable Long regionId) {
    var rows = regionNewsService.getWeeklyNews(regionId);
    return ResponseEntity.ok(BaseResponse.success("ok", rows));
  }

  /** 수동 동기화(관리용) */
  @PostMapping("/dev/sync/regionnnews")
  public ResponseEntity<BaseResponse<Map<String, Object>>> syncAll() {
    int saved = regionNewsService.syncWeeklyAllRegions();
    return ResponseEntity.ok(BaseResponse.success("weekly sync done", Map.of("saved", saved)));
  }

  @Operation(
      summary = "특정 지역 지난주 뉴스 목록 조회 (지역명)",
      description = "지역명을 쿼리스트링으로 전달하면 해당 지역과 ‘서울시’의 지난주(월~일) 뉴스 목록을 최신순으로 반환합니다. (200 OK)")
  @GetMapping("/regions/weeklynews")
  public ResponseEntity<BaseResponse<List<RegionNewsResponse>>> getWeeklyByRegionName(
      @Parameter(description = "지역명", example = "성북구")
          @RequestParam(value = "regionName", defaultValue = "성북구")
          String regionName) {

    List<RegionNewsResponse> list = regionNewsService.getWeeklyNewsByRegionName(regionName);
    return ResponseEntity.ok(BaseResponse.success("특정 지역 지난주 뉴스 목록 조회 성공", list));
  }
}
