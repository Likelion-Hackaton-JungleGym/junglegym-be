package com.hackathon.junglegym.domain.politician.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenApiErrorCode implements BaseErrorCode {
  OPENAPI_HTTP_ERROR("OPENAPI_001", "열린국회정보 API HTTP 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
  OPENAPI_PARSE_ERROR("OPENAPI_002", "열린국회정보 API 응답 파싱에 실패했습니다.", HttpStatus.BAD_GATEWAY),
  OPENAPI_EMPTY_BODY("OPENAPI_003", "열린국회정보 API 응답 파싱에 실패했습니다.", HttpStatus.BAD_GATEWAY),
  OPENAPI_ARRAY_KEY_MISSING("OPENAPI_004", "열린국회정보 API 응답 바디가 비어있습니다.", HttpStatus.BAD_GATEWAY);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
