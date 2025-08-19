package com.hackathon.junglegym.domain.newsletter.mapper;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
import com.hackathon.junglegym.domain.newsletter.dto.request.NewsletterCreateRequest;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterListItemResponse;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterResponse;
import com.hackathon.junglegym.domain.newsletter.entity.Newsletter;
import com.hackathon.junglegym.domain.region.entity.Region;

public class NewsletterMapper {

  // Request + Region + MediaOrientation → Entity
  public static Newsletter toEntity(
      NewsletterCreateRequest request, Region region, MediaOrientation media) {
    return Newsletter.builder()
        .region(region)
        .mediaOrientation(media)
        .title(request.getTitle())
        .date(request.getDate())
        .link(request.getLink())
        .thumbnailUrl(request.getThumbnailUrl())
        .inTitle(request.getInTitle())
        .subtitle1(request.getSubtitle1())
        .content1(request.getContent1())
        .subtitle2(request.getSubtitle2())
        .content2(request.getContent2())
        .todayQuestion(request.getTodayQuestion())
        .titleQuestion(request.getTitleQuestion())
        .questionContent(request.getQuestionContent())
        .build();
  }

  public static NewsletterResponse toResponse(Newsletter entity) {
    return NewsletterResponse.builder()
        .id(entity.getId())
        .regionName(entity.getRegion().getName())
        .media(entity.getMediaOrientation().getMedia())
        .mediaImgUrl(entity.getMediaOrientation().getImgUrl())
        .title(entity.getTitle())
        .date(entity.getDate())
        .link(entity.getLink())
        .thumbnailUrl(entity.getThumbnailUrl())
        .inTitle(entity.getInTitle())
        .subtitle1(entity.getSubtitle1())
        .content1(entity.getContent1())
        .subtitle2(entity.getSubtitle2())
        .content2(entity.getContent2())
        .todayQuestion(entity.getTodayQuestion())
        .titleQuestion(entity.getTitleQuestion())
        .questionContent(entity.getQuestionContent())
        .build();
  }

  public static NewsletterListItemResponse toListItem(Newsletter e) {
    return NewsletterListItemResponse.builder()
        .newsletterId(e.getId())
        .title(e.getTitle())
        .date(e.getDate())
        .content1(e.getContent1())
        .link(e.getLink())
        .thumbnailImg(e.getThumbnailUrl()) // 프론트가 유튜브면 자체 계산, 기사면 이 값 사용
        .build();
  }

  // 업데이트용: 동일 id 유지
  public static Newsletter toUpdatedEntity(
      Newsletter current, NewsletterCreateRequest r, Region region, MediaOrientation media) {

    return Newsletter.builder()
        .id(current.getId())
        .region(region)
        .mediaOrientation(media)
        .title(r.getTitle())
        .date(r.getDate())
        .link(r.getLink())
        .thumbnailUrl(r.getThumbnailUrl())
        .inTitle(r.getInTitle())
        .subtitle1(r.getSubtitle1())
        .content1(r.getContent1())
        .subtitle2(r.getSubtitle2())
        .content2(r.getContent2())
        .todayQuestion(r.getTodayQuestion())
        .titleQuestion(r.getTitleQuestion())
        .questionContent(r.getQuestionContent())
        .build();
  }
}
