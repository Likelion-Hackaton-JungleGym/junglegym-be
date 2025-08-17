package com.hackathon.junglegym.domain.promiseCategory.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromiseCategoryErrorCode implements BaseErrorCode {
  CATEGORY_NOT_FOUND("CATEGORY_001", "공약 카테고리 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CATEGORY_ALREADY_EXISTS("CATEGORY_002", "이미 존재하는 공약카테고리입니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
