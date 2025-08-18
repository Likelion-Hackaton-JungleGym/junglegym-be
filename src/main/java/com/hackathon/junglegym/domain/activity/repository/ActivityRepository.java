package com.hackathon.junglegym.domain.activity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hackathon.junglegym.domain.activity.entity.Activity;
import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

  List<Activity> findByPolitician(Politician politician);

  List<Activity> findByPoliticianId(Long politicianId);

  Optional<Activity> findByPoliticianAndLink(Politician p, String link);

  List<Activity> findTop50ByPoliticianOrderByCreatedAtDesc(Politician p);

  @Query(
      "select a from Activity a "
          + "where a.politician = :p and a.createdAt >= :since "
          + "order by a.createdAt desc")
  List<Activity> findRecentForDedup(@Param("p") Politician p, @Param("since") LocalDateTime since);

  // ActivityRepository.java
  @Query(
      value = "SELECT GET_LOCK(CONCAT('ACT_SYNC_', :politicianId), :waitSec)",
      nativeQuery = true)
  Integer tryLock(@Param("politicianId") Long politicianId, @Param("waitSec") int waitSec);

  @Query(value = "SELECT RELEASE_LOCK(CONCAT('ACT_SYNC_', :politicianId))", nativeQuery = true)
  Integer releaseLock(@Param("politicianId") Long politicianId);
}
