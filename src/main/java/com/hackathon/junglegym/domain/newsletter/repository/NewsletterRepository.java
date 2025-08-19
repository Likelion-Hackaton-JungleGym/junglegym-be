package com.hackathon.junglegym.domain.newsletter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.newsletter.entity.Newsletter;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

  // 지역명 in (요청 지역, "서울시") + 최신 날짜순
  List<Newsletter> findAllByRegion_NameInOrderByDateDesc(List<String> regionNames);
}
