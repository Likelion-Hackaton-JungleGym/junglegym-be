package com.hackathon.junglegym.domain.politician.service;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianRequest;
import com.hackathon.junglegym.domain.politician.dto.request.PoliticianUpdateRequest;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianResponse;
import com.hackathon.junglegym.domain.politician.mapper.PoliticianMapper;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
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
  private final PoliticianMapper politicianMapper;

  // 생성
  @Transactional
  public PoliticianResponse createPolitician(PoliticianRequest request) {

  }

  // 전체 조회
  public List<PoliticianResponse> getAllPolitician() {

  }

  // 단일 조회
  public PoliticianResponse getPoliticianById(Long id) {

  }

  // 수정
  public PoliticianResponse updatePolitician(PoliticianUpdateRequest updateRequest) {

  }

  // 삭제
  public void deletePolitician(String name) {

  }
}
