package com.hackathon.junglegym.domain.activity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.activity.dto.response.ActivityResponse;
import com.hackathon.junglegym.domain.activity.service.ActivityService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Activity", description = "정치인 활동 관련 api")
public class ActivityController {

  private final ActivityService activityService;

  @Operation(
      summary = "특정 정치인 활동 기사 조회",
      description =
          "특정 politicianId에 대해 최초 조회(해당 날짜 기준) 시 Google 뉴스 RSS 기반 최신 활동 기사 3건을 저장, 조회하고, 최초 조회가 아닐 시에는 갱신하지 않고 바로 조회합니다.")
  @GetMapping("/politicians/{politicianId}/activities")
  public ResponseEntity<BaseResponse<List<ActivityResponse>>> getActivities(
      @PathVariable Long politicianId) throws Exception {

    var data = activityService.getActivitiesDaily(politicianId);
    return ResponseEntity.ok(BaseResponse.success("활동 3개 조회 완료", data));
  }
}
