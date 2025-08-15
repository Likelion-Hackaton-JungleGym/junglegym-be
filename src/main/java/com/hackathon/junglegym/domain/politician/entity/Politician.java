package com.hackathon.junglegym.domain.politician.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianUpdateRequest;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "politician")
public class Politician extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "politician_id", nullable = false)
  private Long id;

  // FK: region_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region; // 속한 지역

  @Column(name = "name", nullable = true)
  private String name; // 정치인 이름

  @Column(name = "poly_name", nullable = true)
  private String polyName; // 정당 이름

  // NATIONAL_ASSEMBLY(국회의원), MAYOR_PROVINCIAL(광역자치단체장), MAYOR_MUNICIPAL(기초자치단체장)
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role; // 역할 이름

  @Column(name = "committee", nullable = true)
  private String committee;

  @Column(name = "birth", nullable = true)
  private String birth;

  @Column(name = "retry_number")
  private Integer retryNumber; // 재선여부(횟수): 예) 2선

  @Column(name = "retry_unit")
  private String retryUnit; // 당선된 선거: 예) 제21대, 제22대

  @Column(name = "profile_img")
  private String profileImg; // 프로필 사진(누끼 처리된 이미지 URL)

  @Column(name = "career_summary", columnDefinition = "TEXT")
  private String careerSummary; // 약력 요약

  @Column(name = "military")
  private String military; // 병역 사항

  @Column(name = "role_name")
  private String roleName; // 역할 이름(표기용): 예) 서울특별시장, 성북구청장 등

  // 관할 지역(표기용 텍스트): 예) 성북동, 삼선동, 동선동, 돈암제2동, 안암동, 보문동, 정릉제1동 ...
  @Column(name = "region_text")
  private String regionText;

  public void updatePolitician(
      String name,
      String polyName,
      String committee,
      String birth,
      String retryUnit,
      Integer retryNumber,
      String careerSummary,
      String roleName,
      Region region) {
    this.name = name;
    this.polyName = polyName;
    this.committee = committee;
    this.birth = birth;
    this.retryUnit = retryUnit;
    this.retryNumber = retryNumber;
    this.careerSummary = careerSummary;
    this.roleName = roleName;
    this.region = region;
    this.role = Role.NATIONAL_ASSEMBLY;
  }

  public void updatePolitician(PoliticianUpdateRequest request, Region region) {
    if (request.getUpdateName() != null) {
      this.name = request.getUpdateName();
    }
    if (request.getPolyName() != null) {
      this.polyName = request.getPolyName();
    }
    if (request.getCommittee() != null) {
      this.committee = request.getCommittee();
    }
    if (request.getBirth() != null) {
      this.birth = request.getBirth();
    }
    if (request.getRetryUnit() != null) {
      this.retryUnit = request.getRetryUnit();
    }
    if (request.getRetryNumber() != null) {
      this.retryNumber = request.getRetryNumber();
    }
    if (request.getCareerSummary() != null) {
      this.careerSummary = request.getCareerSummary();
    }
    if (request.getRoleName() != null) {
      this.roleName = request.getRoleName();
    }
    if (region != null) {
      this.region = region;
    }
    if (request.getRole() != null) {
      this.role = request.getRole();
    }
    if (request.getMilitary() != null) {
      this.military = request.getMilitary();
    }
    if (request.getRegionText() != null) {
      this.regionText = request.getRegionText();
    }
  }

  public void updatePolitician(PoliticianUpdateRequest request) {
    if (request.getUpdateName() != null) {
      this.name = request.getUpdateName();
    }
    if (request.getPolyName() != null) {
      this.polyName = request.getPolyName();
    }
    if (request.getCommittee() != null) {
      this.committee = request.getCommittee();
    }
    if (request.getBirth() != null) {
      this.birth = request.getBirth();
    }
    if (request.getRetryUnit() != null) {
      this.retryUnit = request.getRetryUnit();
    }
    if (request.getRetryNumber() != null) {
      this.retryNumber = request.getRetryNumber();
    }
    if (request.getCareerSummary() != null) {
      this.careerSummary = request.getCareerSummary();
    }
    if (request.getRoleName() != null) {
      this.roleName = request.getRoleName();
    }
    if (request.getRole() != null) {
      this.role = request.getRole();
    }
    if (request.getMilitary() != null) {
      this.military = request.getMilitary();
    }
    if (request.getRegionText() != null) {
      this.regionText = request.getRegionText();
    }
  }

  public void updateImgUrl(String imgUrl) {
    this.profileImg = imgUrl;
  }
}
