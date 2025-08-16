package com.hackathon.junglegym.domain.promise.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromiseErrorCode implements BaseErrorCode {
  PROMISE_ALREADY_EXISTS("PROMISE_RECORD_001", "이미 존재하는 공약입니다.", HttpStatus.CONFLICT),
  PROMISE_RECORD_NOT_FOUND("PROMISE_RECORD_002", "공약 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
