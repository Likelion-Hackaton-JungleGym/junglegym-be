package com.hackathon.junglegym.domain.activity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// delete 호출 시 실제 삭제 대신 is_deleted = true 로 업데이트
@SQLDelete(sql = "UPDATE activity SET is_deleted = true WHERE activity_id = ?")
// 기본 조회는 is_deleted = false 인 행만
@Where(clause = "is_deleted = false")
public class Activity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "activity_id", nullable = false)
  private Long id;

  // FK: politician_id (NOT NULL)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "politician_id", nullable = false)
  private Politician politician;

  @Column(name = "title")
  private String title; // 기사 제목

  @Column(name = "link", columnDefinition = "TEXT")
  private String link; // 기사 링크

  // F: 프론트에 제공, T: 제공 X
  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false; // 삭제 여부
}
