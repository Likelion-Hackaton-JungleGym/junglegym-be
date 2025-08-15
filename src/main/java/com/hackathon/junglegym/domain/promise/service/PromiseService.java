package com.hackathon.junglegym.domain.promise.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseProgressSummaryResponse;
import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.entity.Promise;
import com.hackathon.junglegym.domain.promise.mapper.PromiseMapper;
import com.hackathon.junglegym.domain.promise.repository.PromiseRepository;
import com.hackathon.junglegym.domain.promiseCategory.exception.PromiseCategoryErrorCode;
import com.hackathon.junglegym.domain.promiseCategory.repository.PromiseCategoryRepository;
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
}
