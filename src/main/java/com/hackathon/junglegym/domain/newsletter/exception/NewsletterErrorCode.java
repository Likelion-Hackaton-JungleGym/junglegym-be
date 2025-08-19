package com.hackathon.junglegym.domain.newsletter.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsletterErrorCode implements BaseErrorCode {
  NEWSLETTER_EXISTS("NEWSLETTER_001", "이미 존재하는 뉴스레터입니다.", HttpStatus.CONFLICT),
  NEWSLETTER_NOT_FOUND("NEWSLETTER_002", "해당 뉴스레터 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
