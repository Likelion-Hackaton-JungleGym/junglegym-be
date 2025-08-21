package com.hackathon.junglegym.domain.regionNews.mapper;

import com.hackathon.junglegym.domain.regionNews.dto.response.RegionNewsResponse;
import com.hackathon.junglegym.domain.regionNews.entity.RegionNews;

public class RegionNewsMapper {

  public static RegionNewsResponse toResponse(RegionNews e) {
    return RegionNewsResponse.builder()
        .id(e.getId())
        .newsCategory(e.getCategory().getDescription())
        .title(e.getTitle())
        .oneLineContent(e.getOneLineContent())
        .summary(e.getSummary())
        .media(e.getMediaOrientation() != null ? e.getMediaOrientation().getMedia() : null)
        .mediaImgUrl(e.getMediaOrientation() != null ? e.getMediaOrientation().getImgUrl() : null)
        .date(e.getDate())
        .link(e.getLink())
        .build();
  }
}
