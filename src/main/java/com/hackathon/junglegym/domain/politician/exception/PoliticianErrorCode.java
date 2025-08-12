package com.hackathon.junglegym.domain.politician.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PoliticianErrorCode {
  POLITICIAN_ALREADY_EXISTS("POLITICIAN_001", "이미 존재하는 정치인입니다.", HttpStatus.CONFLICT),
  POLITICIAN_NOT_FOUND("POLITICIAN_002", "정치인 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
