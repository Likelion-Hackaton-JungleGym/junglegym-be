package com.hackathon.junglegym.domain.criminalRecord.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CriminalRecordErrorCode implements BaseErrorCode {
  CRIMINAL_RECORD_ALREADY_EXISTS("CRIMINAL_RECORD_001", "이미 존재하는 전과입니다.", HttpStatus.CONFLICT),
  CRIMINAL_RECORD_NOT_FOUND("CRIMINAL_RECORD_002", "전과 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
