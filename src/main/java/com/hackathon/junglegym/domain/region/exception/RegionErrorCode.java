package com.hackathon.junglegym.domain.region.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegionErrorCode implements BaseErrorCode {
  REGION_ALREADY_EXISTS("REGION_001", "이미 존재하는 지역입니다.", HttpStatus.CONFLICT),
  REGION_NOT_FOUND("REGION_002", "지역 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
