package com.hackathon.junglegym.domain.dictionary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.dictionary.dto.request.DictionaryCreateRequest;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryDetailResponse;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryListItemResponse;
import com.hackathon.junglegym.domain.dictionary.entity.Dictionary;
import com.hackathon.junglegym.domain.dictionary.exception.DictionaryErrorCode;
import com.hackathon.junglegym.domain.dictionary.mapper.DictionaryMapper;
import com.hackathon.junglegym.domain.dictionary.repository.DictionaryRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {

  private final DictionaryRepository dictionaryRepository;

  @Transactional
  public Dictionary createDictionary(DictionaryCreateRequest request) {
    // 중복 방지 (예: keyword + title)
    if (dictionaryRepository.existsByKeywordAndTitle(request.getKeyword(), request.getTitle())) {
      throw new CustomException(DictionaryErrorCode.DICTIONARY_EXISTS);
    }
    Dictionary dictionary = DictionaryMapper.toEntity(request);
    return dictionaryRepository.save(dictionary);
  }

  // 전체 목록 조회(비페이징)
  @Transactional
  public List<DictionaryListItemResponse> getAll() {
    return dictionaryRepository.findAllByOrderByIdDesc().stream()
        .map(DictionaryMapper::toListItemResponse)
        .toList();
  }

  // 단건 상세
  @Transactional
  public DictionaryDetailResponse getById(Long id) {
    Dictionary d =
        dictionaryRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(DictionaryErrorCode.DICTIONARY_NOT_FOUND));
    return DictionaryMapper.toDetailResponse(d);
  }

  // 수정 (PUT)
  @Transactional
  public Dictionary updateDictionary(Long id, DictionaryCreateRequest request) {
    Dictionary current =
        dictionaryRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(DictionaryErrorCode.DICTIONARY_NOT_FOUND));

    // (선택) 자신 제외 중복 검사: keyword+title 기준
    boolean exists =
        dictionaryRepository.existsByKeywordAndTitle(request.getKeyword(), request.getTitle());
    boolean sameAsSelf =
        current.getKeyword().equals(request.getKeyword())
            && current.getTitle().equals(request.getTitle());
    if (exists && !sameAsSelf) {
      throw new CustomException(DictionaryErrorCode.DICTIONARY_EXISTS);
    }

    // 엔티티가 불변 스타일이라면 새로 빌드해서 save (id 유지)
    Dictionary updated = DictionaryMapper.toUpdatedEntity(current, request);
    return dictionaryRepository.save(updated);
  }

  // 삭제 (DELETE)
  @Transactional
  public void deleteDictionary(Long id) {
    if (!dictionaryRepository.existsById(id)) {
      throw new CustomException(DictionaryErrorCode.DICTIONARY_NOT_FOUND);
    }
    dictionaryRepository.deleteById(id);
  }
}
