package com.hackathon.junglegym.domain.mediaOrientation.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaOrientationErrorCode implements BaseErrorCode {
  MEDIA_NOT_FOUND("MEDIA_002", "해당 미디어 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
