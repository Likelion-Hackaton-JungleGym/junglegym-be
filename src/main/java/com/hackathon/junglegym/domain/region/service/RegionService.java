package com.hackathon.junglegym.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.region.dto.request.RegionRequest;
import com.hackathon.junglegym.domain.region.dto.request.RegionUpdateRequest;
import com.hackathon.junglegym.domain.region.dto.response.RegionResponse;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.mapper.RegionMapper;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionService {

  private final RegionRepository regionRepository;
  private final RegionMapper regionMapper;

  // 생성
  @Transactional
  public RegionResponse createRegion(RegionRequest request) {
    // 중복 지역명 확인
    if (regionRepository.findByName(request.getName()).isPresent()) {
      throw new CustomException(RegionErrorCode.REGION_ALREADY_EXISTS);
    }

    Region region = Region.builder().name(request.getName()).build();

    Region savedRegion = regionRepository.save(region);

    return regionMapper.toRegionResponse(savedRegion);
  }

  // 전체 조회
  public List<RegionResponse> getAllRegion() {
    List<Region> regions = regionRepository.findAll();
    return regions.stream().map(regionMapper::toRegionResponse).toList();
  }

  // 수정
  @Transactional
  public RegionResponse updateRegion(RegionUpdateRequest updateRequest) {
    Region region =
        regionRepository
            .findByName(updateRequest.getName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    region.update(updateRequest.getNewName());

    log.info("[지역명 수정 - 수정 전: {}, 수정 후: {}]", updateRequest.getName(), updateRequest.getNewName());

    return regionMapper.toRegionResponse(region);
  }

  // 삭제
  @Transactional
  public void deleteRegion(String name) {
    Region region =
        regionRepository
            .findByName(name)
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    regionRepository.delete(region);

    log.info("[지역 삭제: {}", region.getName());
  }
}
