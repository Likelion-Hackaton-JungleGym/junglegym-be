package com.hackathon.junglegym.domain.promiseCategory.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryRequest;
import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;

@Component
public class PromiseCategoryMapper {

  // Entity -> Response
  public PromiseCategoryResponse toPromiseCategoryResponse(PromiseCategory promiseCategory) {
    return PromiseCategoryResponse.builder()
        .categoryId(promiseCategory.getId())
        .title(promiseCategory.getTitle())
        .content(promiseCategory.getContent())
        .build();
  }

  // Request -> Entity
  public PromiseCategory toPromiseCategory(PromiseCategoryRequest request, Politician politician) {
    return PromiseCategory.builder()
        .politician(politician)
        .title(request.getTitle())
        .content(request.getContent())
        .build();
  }
}
