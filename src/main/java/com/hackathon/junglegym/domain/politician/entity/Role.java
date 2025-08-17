package com.hackathon.junglegym.domain.politician.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  NATIONAL_ASSEMBLY("국회의원"),
  MAYOR_PROVINCIAL("광역자치단체장"),
  MAYOR_MUNICIPAL("기초자치단체장");

  private final String description;

  @JsonValue
  public String getRole() {
    return description;
  }

  @JsonCreator()
  public static Role fromValue(String value) {
    for (Role role : Role.values()) {
      if (role.description.equalsIgnoreCase(value)) {
        return role;
      }
    }
    throw new CustomException(PoliticianErrorCode.POLITICIAN_ROLE_NOT_FOUND);
  }
}
