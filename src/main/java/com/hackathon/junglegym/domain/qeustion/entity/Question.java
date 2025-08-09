package com.hackathon.junglegym.domain.qeustion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "question")
public class Question extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id", nullable = false)
  private Long id;

  @Column(name = "question", nullable = false, columnDefinition = "TEXT")
  private String question; // 질문 본문 (필수)

  @Column(name = "answer", columnDefinition = "TEXT")
  private String answer; // AI 답변 (선택)

  @Column(name = "constitution", columnDefinition = "TEXT")
  private String constitution; // 관련 헌법/법령 표기 (선택)
}
