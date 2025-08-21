package com.hackathon.junglegym.domain.regionNews.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.regionNews.entity.RegionNews;

public interface RegionNewsRepository extends JpaRepository<RegionNews, Long> {

  List<RegionNews> findByRegionAndDateBetweenOrderByCreatedAtDesc(
      Region region, LocalDate start, LocalDate end);

  List<RegionNews> findTop5ByRegionAndDateOrderByCreatedAtDesc(Region region, LocalDate date);

  List<RegionNews> findByRegionAndDate(Region region, LocalDate date);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "update RegionNews r set r.isDeleted = true "
          + "where r.region.id = :regionId and r.date between :from and :to and r.isDeleted = false")
  int softDeleteRange(
      @Param("regionId") Long regionId, @Param("from") LocalDate from, @Param("to") LocalDate to);

  // 지역 "이름" 리스트로 + 기간 필터 + 최신순
  List<RegionNews> findByRegion_NameInAndDateBetweenOrderByCreatedAtDesc(
      List<String> regionNames, LocalDate start, LocalDate end);
}
