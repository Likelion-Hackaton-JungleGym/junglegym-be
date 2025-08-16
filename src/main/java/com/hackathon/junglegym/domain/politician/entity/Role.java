package com.hackathon.junglegym.domain.politician.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  NATIONAL_ASSEMBLY("국회의원"),
  MAYOR_PROVINCIAL("광역자치단체장"),
  MAYOR_MUNICIPAL("기초자치단체장");

  private final String description;

  @JsonCreator
  public static Role fromValue(String value) {
    return switch (value) {
      case "국회의원" -> Role.NATIONAL_ASSEMBLY;
      case "광역자치단체장" -> Role.MAYOR_PROVINCIAL;
      case "기초자치단체장" -> Role.MAYOR_MUNICIPAL;
      default -> null;
    };
  }
}
