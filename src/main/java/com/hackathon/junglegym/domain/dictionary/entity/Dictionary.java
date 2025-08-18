package com.hackathon.junglegym.domain.dictionary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dictionary")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dictionary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
  @Column(name = "dictionary_id")
  private Long id;

  @Column(name = "keyword", nullable = true)
  private String keyword;

  @Column(name = "title", nullable = true)
  private String title;

  @Column(name = "subtitle", nullable = true)
  private String subtitle;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;
}
