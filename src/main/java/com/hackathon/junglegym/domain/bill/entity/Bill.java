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

  // 국회 의안 고유ID (중복 방지용)
  @Column(name = "assembly_bill_id", unique = true, length = 50)
  private String assemblyBillId;

  // FK: politician_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician; // 발의자 정치인

  @Column(name = "name")
  private String name; // 법률안 명 BILL_NAME

  @Column(name = "main_content", columnDefinition = "TEXT")
  private String mainContent; // 주요 내용 (상세 API 필요; 일단 null 저장 가능)

  @Column(name = "summary_content", columnDefinition = "TEXT")
  private String summaryContent; // 요약된 내용 (상세 API 필요; 일단 null)

  @Column(name = "result")
  private String result; // 본회의심의결과. PROC_RESULT

  @Column(name = "propose_date")
  private LocalDate proposeDate; // 발의일 (yyyy-MM-dd). PROPOSE_DT

  @Column(name = "detail_link")
  private String detailLink; // 세부사항 링크. PROPOSE_DT

  @Column(name = "main_proposer")
  private String mainProposer; // 대표발의자. RST_PROPOSER

  @Column(name = "join_proposer", columnDefinition = "TEXT")
  private String joinProposer; // 공동발의자. PUBL_PROPOSER

  // 편의 갱신 메서드
  public void updateFromOpenApi(
      Politician politician,
      String assemblyBillId,
      String name,
      LocalDate proposeDate,
      String result,
      String detailLink,
      String mainProposer,
      String joinProposer,
      String mainContent,
      String summaryContent) {

    this.politician = politician;
    this.assemblyBillId = assemblyBillId;
    this.name = name;
    this.proposeDate = proposeDate;
    this.result = result;
    this.detailLink = detailLink;
    this.mainProposer = mainProposer;
    this.joinProposer = joinProposer;
    this.mainContent = mainContent;
    this.summaryContent = summaryContent;
  }

  public void updateMainContent(String mainContent) {
    this.mainContent = mainContent;
  }

  public void updateSummaryContent(String summaryContent) {
    this.summaryContent = summaryContent;
  }
}
