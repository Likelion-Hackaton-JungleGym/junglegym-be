package com.hackathon.junglegym.domain.regionNews.mapper;

import com.hackathon.junglegym.domain.regionNews.dto.response.RegionNewsResponse;
import com.hackathon.junglegym.domain.regionNews.entity.RegionNews;

public class RegionNewsMapper {

  public static RegionNewsResponse toResponse(RegionNews e) {
    String media =
        (e.getMediaOrientation() != null)
            ? e.getMediaOrientation().getMedia()
            : e.getMediaName(); // 저장 시 제목에서 꼬리표 제거했으니 여기서 굳이 재추출할 필요 X

    String mediaImgUrl =
        (e.getMediaOrientation() != null) ? e.getMediaOrientation().getImgUrl() : null;

    return RegionNewsResponse.builder()
        .id(e.getId())
        .newsCategory(e.getCategory().getDescription())
        .title(e.getTitle()) // 이미 clean title
        .oneLineContent(e.getOneLineContent())
        .summary(e.getSummary())
        .media(media)
        .mediaImgUrl(mediaImgUrl)
        .date(e.getDate())
        .link(e.getLink())
        .build();
  }
}
