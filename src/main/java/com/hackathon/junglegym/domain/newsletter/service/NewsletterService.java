package com.hackathon.junglegym.domain.newsletter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
import com.hackathon.junglegym.domain.mediaOrientation.exception.MediaOrientationErrorCode;
import com.hackathon.junglegym.domain.mediaOrientation.repository.MediaOrientationRepository;
import com.hackathon.junglegym.domain.newsletter.dto.request.NewsletterCreateRequest;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterListItemResponse;
import com.hackathon.junglegym.domain.newsletter.dto.response.NewsletterResponse;
import com.hackathon.junglegym.domain.newsletter.entity.Newsletter;
import com.hackathon.junglegym.domain.newsletter.exception.NewsletterErrorCode;
import com.hackathon.junglegym.domain.newsletter.mapper.NewsletterMapper;
import com.hackathon.junglegym.domain.newsletter.repository.NewsletterRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

  private final NewsletterRepository newsletterRepository;
  private final RegionRepository regionRepository;
  private final MediaOrientationRepository mediaOrientationRepository;

  // NewsletterService.java
  @Transactional
  public NewsletterResponse createNewsletter(NewsletterCreateRequest request) {
    // 1) 지역명으로 Region 조회
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    // 2) 언론사명(문자열)으로 MediaOrientation 조회
    String mediaKey = request.getMedia() != null ? request.getMedia().trim() : null;
    MediaOrientation media =
        mediaOrientationRepository
            .findById(mediaKey)
            .orElseThrow(() -> new CustomException(MediaOrientationErrorCode.MEDIA_NOT_FOUND));

    // 2) 엔티티 생성
    Newsletter entity = NewsletterMapper.toEntity(request, region, media);

    // 3) 저장
    Newsletter saved = newsletterRepository.save(entity);

    // 4) 응답 DTO로 매핑 (연관 필드 접근은 트랜잭션 안에서 안전)
    return NewsletterMapper.toResponse(saved);
  }

  /** 지역명으로 목록 조회 (요청 지역 + '서울시') */
  @Transactional(readOnly = true)
  public List<NewsletterListItemResponse> getNewslettersByRegion(String regionName) {
    String name = regionName == null ? "" : regionName.trim();

    // 1) 지역 검증 ("서울시"는 예외 처리 가능)
    if (!"서울시".equals(name)) {
      regionRepository
          .findByName(name)
          .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));
    }

    // 2) 해당 지역 + "서울시" 뉴스레터 조회 (최신순)
    List<Newsletter> list =
        newsletterRepository.findAllByRegion_NameInOrderByDateDesc(List.of(name, "서울시"));

    // 3) DTO 변환
    return list.stream().map(NewsletterMapper::toListItem).toList();
  }

  @Transactional(readOnly = true)
  public NewsletterResponse getNewsletterById(Long id) {
    Newsletter n =
        newsletterRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(NewsletterErrorCode.NEWSLETTER_NOT_FOUND));
    return NewsletterMapper.toResponse(n);
  }

  // 수정 (PUT)
  @Transactional
  public NewsletterResponse updateNewsletter(Long id, NewsletterCreateRequest request) {
    Newsletter current =
        newsletterRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(NewsletterErrorCode.NEWSLETTER_NOT_FOUND));

    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    MediaOrientation media =
        mediaOrientationRepository
            .findById(request.getMedia().trim())
            .orElseThrow(() -> new CustomException(MediaOrientationErrorCode.MEDIA_NOT_FOUND));

    Newsletter updated = NewsletterMapper.toUpdatedEntity(current, request, region, media);
    Newsletter saved = newsletterRepository.save(updated);

    return NewsletterMapper.toResponse(saved);
  }

  // 삭제 (DELETE)
  @Transactional
  public void deleteNewsletter(Long id) {
    if (!newsletterRepository.existsById(id)) {
      throw new CustomException(NewsletterErrorCode.NEWSLETTER_NOT_FOUND);
    }
    newsletterRepository.deleteById(id);
  }
}
