package com.hackathon.junglegym.domain.activity.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.hackathon.junglegym.domain.activity.dto.response.ActivityResponse;
import com.hackathon.junglegym.domain.activity.entity.Activity;

public class ActivityMapper {

  public static ActivityResponse toActivityResponse(Activity e) {
    return ActivityResponse.builder()
        .activityId(e.getId())
        .title(e.getTitle())
        .link(e.getLink())
        .build();
  }

  public static List<ActivityResponse> toActivityResponseList(List<Activity> list) {
    return list.stream().map(ActivityMapper::toActivityResponse).collect(Collectors.toList());
  }
}
