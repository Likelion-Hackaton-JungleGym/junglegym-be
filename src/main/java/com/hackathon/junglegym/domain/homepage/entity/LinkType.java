package com.hackathon.junglegym.domain.homepage.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

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

  @JsonCreator
  public static LinkType fromValue(String value) {
    return switch (value) {
      case "인스타그램" -> LinkType.INSTAGRAM;
      case "블로그" -> LinkType.BLOG;
      case "페이스북" -> LinkType.FACEBOOK;
      case "트위터" -> LinkType.TWITTER;
      case "유튜브" -> LinkType.YOUTUBE;
      case "홈페이지" -> LinkType.HOMEPAGE;
      case "전과링크" -> LinkType.FINELINK;
      default -> null;
    };
  }
}
