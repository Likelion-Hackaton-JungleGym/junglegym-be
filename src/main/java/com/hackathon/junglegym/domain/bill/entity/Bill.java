package com.hackathon.junglegym.domain.bill.entity;

import java.time.LocalDate;

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
@Table(name = "bill")
public class Bill extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bill_id", nullable = false)
  private Long id;

  // FK: politician_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician; // 발의자 정치인

  @Column(name = "name")
  private String name; // 법률안 명

  @Column(name = "main_content", columnDefinition = "TEXT")
  private String mainContent; // 주요 내용

  @Column(name = "summary_content", columnDefinition = "TEXT")
  private String summaryContent; // 내용 요약

  @Column(name = "propose_date")
  private LocalDate proposeDate; // 발의일 (yyyy-MM-dd)

  @Column(name = "detail_link")
  private String detailLink; // 세부사항 링크

  @Column(name = "main_proposer")
  private String mainProposer; // 대표발의자

  @Column(name = "join_proposer")
  private String joinProposer; // 공동발의자
}
