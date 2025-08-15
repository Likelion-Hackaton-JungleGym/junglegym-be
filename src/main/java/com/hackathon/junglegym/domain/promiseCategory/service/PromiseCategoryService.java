package com.hackathon.junglegym.domain.promiseCategory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.repository.PromiseCategoryRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromiseCategoryService {

  private final PromiseCategoryRepository promiseCategoryRepository;
  private final PoliticianRepository politicianRepository;

  @Transactional(readOnly = true)
  public List<PromiseCategoryResponse> getCategoriesByPolitician(Long politicianId) {
    if (!politicianRepository.existsById(politicianId)) {
      throw new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND);
    }

    return promiseCategoryRepository.findPromiseCategoriesByPoliticianId(politicianId);
  }
}
