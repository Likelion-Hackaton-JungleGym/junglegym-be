package com.hackathon.junglegym.domain.property.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.property.dto.request.PropertyRequest;
import com.hackathon.junglegym.domain.property.dto.response.PropertyResponse;
import com.hackathon.junglegym.domain.property.entity.Property;
import com.hackathon.junglegym.domain.property.exception.PropertyErrorCode;
import com.hackathon.junglegym.domain.property.mapper.PropertyMapper;
import com.hackathon.junglegym.domain.property.repository.PropertyRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {

  private final PropertyRepository propertyRepository;
  private final PropertyMapper propertyMapper;
  private final PoliticianRepository politicianRepository;
  private final RegionRepository regionRepository;

  // 생성
  @Transactional
  public PropertyResponse createProperty(PropertyRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    // 재산 데이터 존재 시 생성 불가능
    if (propertyRepository.findByPolitician_Id(politician.getId()).isPresent()) {
      throw new CustomException(PropertyErrorCode.PROPERTY_ALREADY_EXISTS);
    }

    Property property = propertyMapper.toProperty(request, politician);

    return propertyMapper.toPropertyResponse(propertyRepository.save(property));
  }

  // 전체 조회
  public PropertyResponse getAllProperty(Long politicianId) {
    Politician politician =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    Property property =
        propertyRepository
            .findByPolitician_Id(politicianId)
            .orElseThrow(() -> new CustomException(PropertyErrorCode.PROPERTY_NOT_FOUND));
    return propertyMapper.toPropertyResponse(property);
  }

  // 수정
  @Transactional
  public PropertyResponse updateProperty(PropertyRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    Property property =
        propertyRepository
            .findByPolitician_Id(politician.getId())
            .orElseThrow(() -> new CustomException(PropertyErrorCode.PROPERTY_NOT_FOUND));

    property.update(request);

    return (propertyMapper.toPropertyResponse(property));
  }

  // 삭제
  @Transactional
  public void deleteProperty(String regionName, String name) {
    Region region =
        regionRepository
            .findByName(regionName)
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(name, region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    Property property =
        propertyRepository
            .findByPolitician_Id(politician.getId())
            .orElseThrow(() -> new CustomException(PropertyErrorCode.PROPERTY_NOT_FOUND));

    propertyRepository.deleteByPolitician(politician);
  }
}
