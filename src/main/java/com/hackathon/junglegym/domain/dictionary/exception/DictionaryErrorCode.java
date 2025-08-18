package com.hackathon.junglegym.domain.dictionary.exception;

import org.springframework.http.HttpStatus;

import com.hackathon.junglegym.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DictionaryErrorCode implements BaseErrorCode {
  DICTIONARY_EXISTS("DICTIONARY_001", "이미 존재하는 사전입니다.", HttpStatus.CONFLICT),
  DICTIONARY_NOT_FOUND("DICTIONARY_002", "해당 정글 사전 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
