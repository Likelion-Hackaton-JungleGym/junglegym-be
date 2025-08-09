package com.hackathon.junglegym.domain.property.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
@Table(name = "property")
public class Property extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "property_id", nullable = false)
  private Long id;

  // FK: politician_id (NOT NULL)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician;

  @Column(name = "total_capital")
  private Long totalCapital; // 총 자산 (건물/부동산/예금/정치자금 현재가액 합)

  @Column(name = "total_debt")
  private Long totalDebt; // 총 부채 (채무 현재가액)

  @Column(name = "capital")
  private Long capital; // 순 자산 = 총 자산 - 총 부채
}
