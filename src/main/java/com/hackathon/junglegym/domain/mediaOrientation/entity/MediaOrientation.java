package com.hackathon.junglegym.domain.mediaOrientation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "media_orientation")
public class MediaOrientation extends BaseTimeEntity {

  @Id
  @Column(name = "media", nullable = false)
  private String media; // 언론사명 (PK)

  @Column(name = "img_url")
  private String imgUrl; // 정치성향 이미지 URL
}
