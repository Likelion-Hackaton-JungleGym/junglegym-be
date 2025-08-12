package com.hackathon.junglegym.domain.politician.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianRequest;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianResponse;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.mapper.PoliticianMapper;
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
  //
  //  // 전체 조회
  //  public List<PoliticianResponse> getAllPolitician() {
  //    List<Politician> politicianList = politicianRepository.findAll();
  //  }
  //
  //  // 지역별 전체 조회
  //  public List<PoliticianResponse> getAllPoliticianByRegion(String regionName) {}
  //
  //  // 단일 조회
  //  public PoliticianResponse getPoliticianById(Long id) {}
  //
  //  // 수정
  //  public PoliticianResponse updatePolitician(PoliticianUpdateRequest updateRequest) {}
  //
  //  // 삭제
  //  public void deletePolitician(String name) {}
}
