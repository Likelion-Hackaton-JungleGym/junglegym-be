package com.hackathon.junglegym.domain.promiseCategory.entity;

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
import com.hackathon.junglegym.domain.promiseCategory.dto.request.PromiseCategoryUpdateRequest;
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
@Table(name = "promise_category")
public class PromiseCategory extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "promise_category_id", nullable = false)
  private Long id;

  // FK: politician_id 정치인 id
  @ManyToOne(fetch = FetchType.LAZY, optional = false) // JPA 레벨 null 방지
  @JoinColumn(name = "politician_id", nullable = false) // DB 레벨 null 방지
  private Politician politician;

  @Column(name = "title")
  private String title; // 공약 분야 제목

  @Column(name = "content")
  private String content; // 공약 분야 텍스트

  public void update(PromiseCategoryUpdateRequest request) {
    if (request.getNewTitle() != null) {
      this.title = request.getNewTitle();
    }
    if (request.getNewContent() != null) {
      this.content = request.getNewContent();
    }
  }
}
