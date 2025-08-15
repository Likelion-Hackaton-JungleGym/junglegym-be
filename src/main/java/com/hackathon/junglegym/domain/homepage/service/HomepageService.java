package com.hackathon.junglegym.domain.homepage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.homepage.dto.request.HomepageDeleteRequest;
import com.hackathon.junglegym.domain.homepage.dto.request.HomepageRequest;
import com.hackathon.junglegym.domain.homepage.dto.response.HomepageResponse;
import com.hackathon.junglegym.domain.homepage.entity.Homepage;
import com.hackathon.junglegym.domain.homepage.exception.HomepageErrorCode;
import com.hackathon.junglegym.domain.homepage.mapper.HomepageMapper;
import com.hackathon.junglegym.domain.homepage.repository.HomepageRepository;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomepageService {

  private final HomepageRepository homepageRepository;
  private final HomepageMapper homepageMapper;
  private final PoliticianRepository politicianRepository;
  private final RegionRepository regionRepository;

  // 생성
  @Transactional
  public HomepageResponse createHomepage(HomepageRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    // 같은 정치인 + 같은 링크타입은 1건만 허용
    if (homepageRepository.existsByPoliticianAndLinkType(politician, request.getLinkType())) {
      throw new CustomException(HomepageErrorCode.HOMEPAGE_TYPE_ALREADY_EXISTS);
    }

    Homepage homepage = homepageMapper.toHomepage(request, politician);

    return homepageMapper.toHomepageResponse(homepageRepository.save(homepage));
  }

  // 전체 조회
  public List<HomepageResponse> getAllHomepage(Long politicianId) {
    List<Homepage> homepages = homepageRepository.findAllByPolitician_Id(politicianId);
    return homepages.stream().map(homepageMapper::toHomepageResponse).toList();
  }

  // 수정
  @Transactional
  public HomepageResponse updateHomepage(HomepageRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    Homepage homepage =
        homepageRepository.findByPoliticianAndLinkType(politician, request.getLinkType());

    homepage.update(request);

    return (homepageMapper.toHomepageResponse(homepage));
  }

  // 삭제
  @Transactional
  public void deleteHomepage(HomepageDeleteRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    Homepage homepage =
        homepageRepository.findByPoliticianAndLinkType(politician, request.getLinkType());

    homepageRepository.delete(homepage);
  }
}
