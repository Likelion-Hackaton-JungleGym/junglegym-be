package com.hackathon.junglegym.domain.dictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.dictionary.entity.Dictionary;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

  List<Dictionary> findAllByOrderByIdDesc(); // 전체 정렬

  // 중복 체크 (원하면 기준을 바꾸세요: keyword+title)
  boolean existsByKeywordAndTitle(String keyword, String title);
}
