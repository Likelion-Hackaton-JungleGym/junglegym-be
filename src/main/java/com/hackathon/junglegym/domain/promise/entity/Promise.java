package com.hackathon.junglegym.domain.promise.entity;

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

import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;
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
@Table(name = "promise")
public class Promise extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "promise_id", nullable = false)
  private Long id;

  // FK: promise_category_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "promise_category_id", nullable = false)
  private PromiseCategory category; // 공약 카테고리

  @Column(name = "name")
  private String name; // 공약명

  @Column(name = "content", columnDefinition = "TEXT") // 길이 제한 X
  private String content; // 공약 내용

  @Enumerated(EnumType.STRING)
  @Column(name = "progress", length = 50)
  private PromiseProgress progress; // 진행상황

  @Column(name = "status_summary", columnDefinition = "TEXT")
  private String statusSummary; // 공약 사업 관련 현황

  @Column(name = "goal", columnDefinition = "TEXT")
  private String goal; // 정책 목표

  @Column(name = "outline", columnDefinition = "TEXT")
  private String outline; // 추진 개요

  @Column(name = "status", columnDefinition = "TEXT")
  private String status; // 추진 결과

  @Column(name = "effect", columnDefinition = "TEXT")
  private String effect; // 기대 효과

  @Column(name = "plan", columnDefinition = "TEXT")
  private String plan; // 향후 추진 계획
}
