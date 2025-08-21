package com.hackathon.junglegym.domain.regionNews.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NewsCategory {
  POLITICS("정치"),
  ECONOMY("경제"),
  SOCIETY("사회"),
  WORLD("세계"),
  LIFE_CULTURE("생활/문화"),
  IT_SCIENCE("IT/과학");

  private final String description;
}
