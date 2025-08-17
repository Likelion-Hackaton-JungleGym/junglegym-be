package com.hackathon.junglegym.domain.promise.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.promise.dto.request.PromiseDeleteRequest;
import com.hackathon.junglegym.domain.promise.dto.request.PromiseRequest;
import com.hackathon.junglegym.domain.promise.dto.request.PromiseUpdateRequest;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseProgressSummaryResponse;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.entity.Promise;
import com.hackathon.junglegym.domain.promise.exception.PromiseErrorCode;
import com.hackathon.junglegym.domain.promise.mapper.PromiseMapper;
import com.hackathon.junglegym.domain.promise.repository.PromiseRepository;
import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;
import com.hackathon.junglegym.domain.promiseCategory.exception.PromiseCategoryErrorCode;
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
public class PromiseService {

  private final PromiseRepository promiseRepository;
  private final PoliticianRepository politicianRepository;
  private final PromiseCategoryRepository promiseCategoryRepository;
  private final RegionRepository regionRepository;

  /** 특정 정치인의 공약 이행 현황 요약 */
  @Transactional(readOnly = true)
  public PromiseProgressSummaryResponse getProgressSummaryByPolitician(Long politicianId) {
    // 정치인 존재 검증 (없으면 예외)
    Politician politician =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    // 해당 정치인 소속 공약 전부 조회
    List<Promise> promises = promiseRepository.findByCategory_Politician_Id(politician.getId());

    // Mapper로 DTO 변환(개수/소계/비율/업데이트일 계산)
    return PromiseMapper.toProgressSummary(promises);
  }

  @Transactional(readOnly = true)
  public List<PromiseResponse> getPromisesByCategory(Long categoryId) {
    if (!promiseCategoryRepository.existsById(categoryId)) {
      throw new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND);
    }
    return promiseRepository.findPromisesByCategoryId(categoryId);
  }

  // 생성
  @Transactional
  public PromiseResponse createPromise(PromiseRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getPoliticianName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    PromiseCategory promiseCategory =
        promiseCategoryRepository
            .findByPoliticianAndTitle(politician, request.getCategory())
            .orElseThrow(() -> new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND));

    if (promiseRepository.findByCategoryAndName(promiseCategory, request.getName()) != null) {
      throw new CustomException(PromiseErrorCode.PROMISE_ALREADY_EXISTS);
    }

    Promise promise = PromiseMapper.toPromise(request, promiseCategory);

    return PromiseMapper.toPromiseResponse(promiseRepository.save(promise));
  }

  // 수정
  @Transactional
  public PromiseResponse updatePromise(PromiseUpdateRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getPoliticianName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    PromiseCategory promiseCategory =
        promiseCategoryRepository
            .findByPoliticianAndTitle(politician, request.getCategory())
            .orElseThrow(() -> new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND));

    Promise promise = promiseRepository.findByCategoryAndName(promiseCategory, request.getName());

    if (promise == null) {
      throw new CustomException(PromiseErrorCode.PROMISE_RECORD_NOT_FOUND);
    }

    promise.update(request);

    return (PromiseMapper.toPromiseResponse(promise));
  }

  // 삭제
  @Transactional
  public void deletePromise(PromiseDeleteRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getPoliticianName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    PromiseCategory promiseCategory =
        promiseCategoryRepository
            .findByPoliticianAndTitle(politician, request.getCategory())
            .orElseThrow(() -> new CustomException(PromiseCategoryErrorCode.CATEGORY_NOT_FOUND));

    Promise promise = promiseRepository.findByCategoryAndName(promiseCategory, request.getName());

    if (promise == null) {
      throw new CustomException(PromiseErrorCode.PROMISE_RECORD_NOT_FOUND);
    }

    promiseRepository.delete(promise);
  }
}
