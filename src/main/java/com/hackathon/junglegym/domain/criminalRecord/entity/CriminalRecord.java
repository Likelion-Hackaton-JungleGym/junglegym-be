package com.hackathon.junglegym.domain.criminalRecord.entity;

import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordUpdateRequest;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "criminal_record")
public class CriminalRecord extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "criminal_record_id", nullable = false)
  private Long id;

  // FK: politician_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician;

  @Column(name = "title")
  private String title; // 전과 제목

  @Column(name = "fine")
  private Long fine; // 벌금 (단위: 원)

  public void update(CriminalRecordUpdateRequest request) {
    if (request.getNewTitle() != null) {
      this.title = request.getNewTitle();
    }
    if (request.getNewFine() != null) {
      this.fine = request.getNewFine();
    }
  }
}
