package com.hackathon.junglegym.domain.politicianIssue.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politicianIssue.entity.PoliticianIssue;

public interface PoliticianIssueRepository extends JpaRepository<PoliticianIssue, Long> {

  List<PoliticianIssue> findByPolitician(Politician politician);

  List<PoliticianIssue> findByPoliticianId(Long politicianId);

  Optional<PoliticianIssue> findByPoliticianAndLink(Politician politician, String link);

  // 최신 3건
  List<PoliticianIssue> findTop3ByPoliticianOrderByCreatedAtDesc(Politician p);

  // 오늘(또는 특정 시각 이후)에 생성된 이슈가 있는지
  boolean existsByPoliticianAndCreatedAtAfter(Politician p, LocalDateTime dateTime);

  // 소프트딜리트를 위해 전체 조회 후 deleteAll 호출
  List<PoliticianIssue> findAllByPolitician(Politician p);

  // --- MySQL 분산락 ---
  @Query(value = "SELECT GET_LOCK(CONCAT('issue:', ?1), ?2)", nativeQuery = true)
  Integer tryLock(Long politicianId, int timeoutSeconds);

  @Query(value = "SELECT RELEASE_LOCK(CONCAT('issue:', ?1))", nativeQuery = true)
  Integer releaseLock(Long politicianId);
}
