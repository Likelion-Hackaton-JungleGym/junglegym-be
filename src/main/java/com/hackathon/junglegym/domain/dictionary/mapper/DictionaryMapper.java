package com.hackathon.junglegym.domain.dictionary.mapper;

import com.hackathon.junglegym.domain.dictionary.dto.request.DictionaryCreateRequest;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryDetailResponse;
import com.hackathon.junglegym.domain.dictionary.dto.response.DictionaryListItemResponse;
import com.hackathon.junglegym.domain.dictionary.entity.Dictionary;

public class DictionaryMapper {

  // Request -> Entity
  public static Dictionary toEntity(DictionaryCreateRequest request) {
    return Dictionary.builder()
        .keyword(request.getKeyword())
        .title(request.getTitle())
        .subtitle(request.getSubtitle())
        .content(request.getContent())
        .build();
  }

  // Entity -> ListItemResponse
  public static DictionaryListItemResponse toListItemResponse(Dictionary d) {
    return DictionaryListItemResponse.builder()
        .id(d.getId())
        .keyword(d.getKeyword())
        .title(d.getTitle())
        .subtitle(d.getSubtitle())
        .build();
  }

  // Entity -> DetailResponse
  public static DictionaryDetailResponse toDetailResponse(Dictionary d) {
    return DictionaryDetailResponse.builder()
        .id(d.getId())
        .keyword(d.getKeyword())
        .title(d.getTitle())
        .subtitle(d.getSubtitle())
        .content(d.getContent())
        .build();
  }

  // DictionaryMapper.java
  public static Dictionary toUpdatedEntity(Dictionary current, DictionaryCreateRequest req) {
    return Dictionary.builder()
        .id(current.getId()) // ★ 기존 ID 유지
        .keyword(req.getKeyword())
        .title(req.getTitle())
        .subtitle(req.getSubtitle())
        .content(req.getContent())
        .build();
  }
}
