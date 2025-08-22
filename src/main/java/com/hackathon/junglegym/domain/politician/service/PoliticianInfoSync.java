package com.hackathon.junglegym.domain.politician.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticianInfoSync {

  private final PoliticianRepository politicianRepository;
  private final RegionRepository regionRepository;
  private final PoliticianInfoOpenApi openApi;

  @Transactional
  public int syncAllByRegions() throws Exception {

    List<JsonNode> rows = openApi.fetchAllPages("row");
    List<Region> regions = regionRepository.findAll();
    int saved = 0;

    for (JsonNode row : rows) {
      String origNm = openApi.text(row, "ORIG_NM");

      if (!isSeoulOrigNm(origNm)) {
        continue;
      }

      Optional<String> guName = extractGuName(origNm);

      if (guName.isEmpty()) {
        continue;
      }

      for (Region region : regions) {
        if (guName.get().equals(region.getName())) {
          if (savePolitician(row, region)) {
            saved++;
          }
        }
      }
    }
    return saved;
  }

  private static boolean isSeoulOrigNm(String origNm) {
    if (origNm == null) return false;
    String first = origNm.trim().split("\\s+")[0];
    return "서울".equals(first);
  }

  @Transactional
  public boolean savePolitician(JsonNode n, Region region) {
    String pName = openApi.text(n, "HG_NM");
    if (pName == null) {
      return false;
    }

    Politician politician =
        politicianRepository
            .findByNameAndRegion(pName, region)
            .orElse(Politician.builder().name(pName).build());

    String name = openApi.text(n, "HG_NM");
    String polyName = openApi.text(n, "POLY_NM");
    String committee = openApi.text(n, "CMIT_NM");
    String birth = formatBirth(openApi.text(n, "BTH_DATE"));
    String retryUnit = openApi.text(n, "UNITS");
    Integer retryNumber = countUnits(retryUnit);
    String careerSummary = openApi.text(n, "MEM_TITLE");
    String roleName = openApi.text(n, "ORIG_NM");

    politician.updatePolitician(
        name, polyName, committee, birth, retryUnit, retryNumber, careerSummary, roleName, region);

    politicianRepository.save(politician);

    log.info("정치인 정보 크롤링 및 업데이트 완료, name: {} region: {}", name, region.getName());

    return true;
  }

  // 생일 포멧 변경 (2000-01-01 -> 2000. 01. 01)
  private static String formatBirth(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }

    try {
      if (raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
        LocalDate d = LocalDate.parse(raw);
        return String.format("%04d. %02d. %02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
      }
      if (raw.matches("\\d{8}")) {
        return String.format(
            "%s. %s. %s", raw.substring(0, 4), raw.substring(4, 6), raw.substring(6, 8));
      }
    } catch (Exception ignore) {
      return null;
    }
    return null;
  }

  // 당선 횟수 카운트
  public static Integer countUnits(String units) {
    if (units == null || units.isBlank()) {
      return null;
    }

    String[] parts = units.split("\\s*,\\s*");
    int count = 0;

    for (String p : parts) {
      if (!p.isBlank()) {
        count++;
      }
    }
    return count;
  }

  // ORIG_NM에서 지역명 추출
  public Optional<String> extractGuName(String origNm) {
    if (origNm == null || origNm.isBlank()) {
      return Optional.empty();
    }

    String normalized = origNm.trim().replaceAll("\\s+", " ");
    String[] parts = normalized.split(" ");
    String s = parts[parts.length - 1];

    Matcher m1 = Pattern.compile("([가-힣]+구)").matcher(s);
    if (m1.find()) {
      return Optional.of(m1.group(1));
    }

    Matcher m2 = Pattern.compile("([가-힣]+)(갑|을|병|정|무|기|경|신)$").matcher(s);
    if (m2.find()) {
      return Optional.of(m2.group(1) + "구");
    }

    return Optional.empty();
  }
}
