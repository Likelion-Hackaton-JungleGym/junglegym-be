package com.hackathon.junglegym.domain.politician.service;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianRequest;
import com.hackathon.junglegym.domain.politician.dto.request.PoliticianUpdateRequest;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianByRegionResponse;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianResponse;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.mapper.PoliticianMapper;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;
import com.hackathon.junglegym.global.s3.dto.S3Response;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliticianService {

  private final PoliticianRepository politicianRepository;
  private final RegionRepository regionRepository;
  private final PoliticianMapper politicianMapper;

  // 생성
  @Transactional
  public PoliticianResponse createPolitician(PoliticianRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    if (request.getRole() == null) {
      throw new CustomException(PoliticianErrorCode.POLITICIAN_ROLE_REQUIRED);
    }

    Politician politician = politicianMapper.toPolitician(request, region);
    return politicianMapper.toPoliticianResponse(politicianRepository.save(politician));
  }

  // 전체 조회
  public List<PoliticianResponse> getAllPolitician() {
    List<Politician> politicianList = politicianRepository.findAll();
    List<PoliticianResponse> responseList = new ArrayList<>();

    for (Politician p : politicianList) {
      responseList.add(politicianMapper.toPoliticianResponse(p));
    }

    return responseList;
  }

  // 지역별 전체 조회
  public List<PoliticianByRegionResponse> getAllPoliticianByRegion(String regionName) {
    List<Politician> politicianList =
        politicianRepository.findAllByRegion_NameIn(List.of(regionName, "서울시"));
    List<PoliticianByRegionResponse> responseList = new ArrayList<>();

    for (Politician p : politicianList) {
      responseList.add(politicianMapper.toPoliticianByRegionResponse(p));
    }

    return responseList;
  }

  // 단일 조회
  public PoliticianResponse getPoliticianById(Long id) {
    Politician politician =
        politicianRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    return (politicianMapper.toPoliticianResponse(politician));
  }

  // 수정
  @Transactional
  public PoliticianResponse updatePolitician(PoliticianUpdateRequest updateRequest) {
    Region region =
        regionRepository
            .findByName(updateRequest.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(updateRequest.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (updateRequest.getUpdateRegionName() != null) {
      Region newRegion =
          regionRepository
              .findByName(updateRequest.getUpdateRegionName())
              .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

      politician.updatePolitician(updateRequest, newRegion);
    } else {
      politician.updatePolitician(updateRequest);
    }

    return (politicianMapper.toPoliticianResponse(politician));
  }

  // 삭제
  @Transactional
  public void deletePolitician(String name, String regionName) {
    Region region =
        regionRepository
            .findByName(regionName)
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(name, region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    politicianRepository.delete(politician);

    log.info("[정치인 삭제] 이름: {}, 지역: {}", name, regionName);
  }

  @Transactional
  public PoliticianResponse createPoliticianImg(String name, String regionName, S3Response imgUrl) {
    Region region =
        regionRepository
            .findByName(regionName)
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(name, region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    politician.updateImgUrl(imgUrl.getImageUrl());

    return (politicianMapper.toPoliticianResponse(politician));
  }
}
