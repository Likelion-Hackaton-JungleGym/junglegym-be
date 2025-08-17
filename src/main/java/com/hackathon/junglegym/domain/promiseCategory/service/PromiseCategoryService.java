package com.hackathon.junglegym.domain.promiseCategory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryDeleteRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryUpdateRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;
import com.hackathon.junglegym.domain.promiseCategory.exception.PromiseCategoryErrorCode;
import com.hackathon.junglegym.domain.promiseCategory.mapper.PromiseCategoryMapper;
import com.hackathon.junglegym.domain.promiseCategory.repository.PromiseCategoryRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromiseCategoryService {

  private final PromiseCategoryRepository promiseCategoryRepository;
  private final PoliticianRepository politicianRepository;
  private final PromiseCategoryMapper mapper;
  private final RegionRepository regionRepository;

  @Transactional(readOnly = true)
  public List<PromiseCategoryResponse> getCategoriesByPolitician(Long politicianId) {
    if (!politicianRepository.existsById(politicianId)) {
      throw new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND);
    }

    return promiseCategoryRepository.findPromiseCategoriesByPoliticianId(politicianId);
  }

  // 생성
  @Transactional
  public PromiseCategoryResponse createPromiseCategory(PromiseCategoryRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (promiseCategoryRepository
        .findByPoliticianAndTitle(politician, request.getTitle())
        .isPresent()) {
      throw new CustomException(PromiseCategoryErrorCode.CATEGORY_ALREADY_EXISTS);
    }

    PromiseCategory promiseCategory = mapper.toPromiseCategory(request, politician);

    return mapper.toPromiseCategoryResponse(promiseCategoryRepository.save(promiseCategory));
  }

  // 수정
  @Transactional
  public PromiseCategoryResponse updatePromiseCategory(PromiseCategoryUpdateRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    PromiseCategory promiseCategory =
        promiseCategoryRepository
            .findByPoliticianAndTitle(politician, request.getTitle())
            .orElseThrow(() -> new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND));

    promiseCategory.update(request);

    return (mapper.toPromiseCategoryResponse(promiseCategory));
  }

  // 삭제
  @Transactional
  public void deletePromiseCategory(PromiseCategoryDeleteRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    PromiseCategory promiseCategory =
        promiseCategoryRepository
            .findByPoliticianAndTitle(politician, request.getTitle())
            .orElseThrow(() -> new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND));

    promiseCategoryRepository.delete(promiseCategory);
  }
}
