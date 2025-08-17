package com.hackathon.junglegym.domain.homepage.entity;

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

import com.hackathon.junglegym.domain.homepage.dto.request.HomepageRequest;
import com.hackathon.junglegym.domain.politician.entity.Politician;
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
@Table(name = "homepage")
public class Homepage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "homepage_id", nullable = false)
  private Long id;

  // FK: politician_id (NOT NULL)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician;

  @Column(name = "link")
  private String link; // 홈페이지 주소

  @Enumerated(EnumType.STRING)
  @Column(name = "link_type", nullable = false)
  private LinkType linkType;

  public void update(HomepageRequest request) {
    this.link = request.getLink();
  }
}
