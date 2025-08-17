package com.hackathon.junglegym.domain.homepage.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hackathon.junglegym.domain.homepage.exception.HomepageErrorCode;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LinkType {
  INSTAGRAM("인스타그램"),
  BLOG("블로그"),
  FACEBOOK("페이스북"),
  TWITTER("트위터"),
  YOUTUBE("유튜브"),
  HOMEPAGE("홈페이지"),
  FINELINK("전과링크");

  private final String linkName;

  @JsonValue
  public String getLinkType() {
    return linkName;
  }

  @JsonCreator
  public static LinkType fromValue(String value) {
    for (LinkType linkType : LinkType.values()) {
      if (linkType.linkName.equalsIgnoreCase(value)) {
        return linkType;
      }
    }
    throw new CustomException(HomepageErrorCode.HOMEPAGE_TYPE_NOT_FOUND);
  }
}
