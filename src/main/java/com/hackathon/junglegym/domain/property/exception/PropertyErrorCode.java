package com.hackathon.junglegym.domain.property.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PropertyErrorCode implements BaseErrorCode {
  PROPERTY_ALREADY_EXISTS("PROPERTY_001", "재산 정보가 이미 존재합니다.", HttpStatus.CONFLICT),
  PROPERTY_NOT_FOUND("PROPERTY_002", "재산 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
