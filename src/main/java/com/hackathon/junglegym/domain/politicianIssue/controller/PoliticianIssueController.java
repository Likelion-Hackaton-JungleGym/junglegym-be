package com.hackathon.junglegym.domain.politicianIssue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.politicianIssue.dto.response.PoliticianIssueResponse;
import com.hackathon.junglegym.domain.politicianIssue.service.PoliticianIssueService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "PoliticianIssue", description = "정치인 이슈 관련 api")
public class PoliticianIssueController {

  private final PoliticianIssueService politicianIssueService;

  @Operation(
      summary = "특정 정치인 이슈 기사 조회",
      description =
          "특정 politicianId에 대해 최초 조회(해당 날짜 기준) 시 Google 뉴스 RSS 기반 최신 이슈 3건을 저장, 조회하고, 최초 조회가 아닐 시에는 갱신하지 않고 바로 조회합니다.")
  @GetMapping("/politicians/{politicianId}/issues")
  public ResponseEntity<BaseResponse<List<PoliticianIssueResponse>>> getIssues(
      @PathVariable Long politicianId) throws Exception {

    var data = politicianIssueService.getIssuesDaily(politicianId);
    return ResponseEntity.ok(BaseResponse.success("이슈 3개 조회 완료", data));
  }
}
