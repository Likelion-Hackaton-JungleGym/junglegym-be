package com.hackathon.junglegym.domain.regionNews.entity;

import java.time.LocalDate;

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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region_news")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// 🔸 소프트 삭제: delete 호출 시 is_deleted = true 로 업데이트
// regionNewsRepository.deleteById(id); 호출 시, 실제 삭제가 아니라, update를 진행
@SQLDelete(sql = "UPDATE region_news SET is_deleted = true WHERE region_news_id = ?")
// 🔸 기본 조회는 is_deleted = false 인 것만
@Where(clause = "is_deleted = false")
public class RegionNews extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "region_news_id", nullable = false)
  private Long id;

  // FK: region_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;

  @Column(name = "category", nullable = false)
  @Enumerated(EnumType.STRING)
  private NewsCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media", referencedColumnName = "media")
  private MediaOrientation mediaOrientation; // 언론사 정치성향 이미지 (media로 조인, 읽기 전용)\

  @Column(name = "media_name")
  private String mediaName; // 추가 : 언론사명 텍스트(denormalized), FK가 없어도 노출용으로 사용

  @Column(name = "title")
  private String title; // 제목

  @Column(name = "oneLineContent")
  private String oneLineContent; // 내용(길 수 있음)

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary; // 내용(길 수 있음)

  @Column(name = "link", columnDefinition = "TEXT")
  private String link; // 원본 링크

  @Column(name = "date")
  private LocalDate date; // 노출 날짜

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false; // 삭제 여부 (기본값 false, -> F면 프론트 제공, T면 제공 X)
}
