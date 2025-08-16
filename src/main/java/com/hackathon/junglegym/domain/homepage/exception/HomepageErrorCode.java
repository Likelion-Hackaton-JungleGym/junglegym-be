package com.hackathon.junglegym.domain.homepage.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HomepageErrorCode implements BaseErrorCode {
  HOMEPAGE_ALREADY_EXISTS("HOMEPAGE_001", "이미 존재하는 홈페이지 주소입니다.", HttpStatus.CONFLICT),
  HOMEPAGE_TYPE_ALREADY_EXISTS("HOMEPAGE_002", "이미 존재하는 홈페이지 타입입니다.", HttpStatus.CONFLICT),
  HOMEPAGE_NOT_FOUND("HOMEPAGE_003", "홈페이지 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  HOMEPAGE_TYPE_NOT_FOUND("HOMEPAGE_004", "홈페이지 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
