package com.hackathon.junglegym.domain.newsletter.entity;

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

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
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
@Table(name = "newsletter")
public class Newsletter extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "newsletter_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", referencedColumnName = "region_id")
  private Region region; // 지역 엔티티 (region_id로 조인, 읽기 전용)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media", referencedColumnName = "media")
  private MediaOrientation mediaOrientation; // 언론사 정치성향 이미지 (media로 조인, 읽기 전용)

  @Column(name = "title", nullable = true)
  private String title;

  @Column(name = "date")
  private LocalDate date; // 노출 날짜

  @Column(name = "link", columnDefinition = "TEXT")
  private String link; // 유튜브/뉴스 링크 (TEXT)

  @Column(name = "thumbnail_url", columnDefinition = "TEXT")
  private String thumbnailUrl; // 기사일 때 서버에 저장한 썸네일 URL (TEXT)

  @Column(name = "in_title", nullable = true)
  private String inTitle;

  @Column(name = "subtitle1", nullable = true)
  private String subtitle1;

  @Column(name = "content1", nullable = true, columnDefinition = "TEXT")
  private String content1; // Markdown

  @Column(name = "subtitle2", nullable = true)
  private String subtitle2;

  @Column(name = "content2", nullable = true, columnDefinition = "TEXT")
  private String content2; // Markdown

  @Column(name = "today_question", nullable = true, columnDefinition = "TEXT")
  private String todayQuestion; // 오늘의 질문 제목

  @Column(name = "title_question", nullable = true, columnDefinition = "TEXT")
  private String titleQuestion; // 질문 타이틀 (TEXT)

  @Column(name = "question_content", nullable = true, columnDefinition = "TEXT")
  private String questionContent; // Markdown  질문 상
}
