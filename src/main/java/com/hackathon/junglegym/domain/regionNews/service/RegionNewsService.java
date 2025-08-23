package com.hackathon.junglegym.domain.regionNews.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
import com.hackathon.junglegym.domain.mediaOrientation.repository.MediaOrientationRepository;
import com.hackathon.junglegym.domain.politicianIssue.dto.NewsItem;
import com.hackathon.junglegym.domain.politicianIssue.service.GoogleNewsRssCrawler;
import com.hackathon.junglegym.domain.politicianIssue.service.OpenAiNewsService;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.domain.regionNews.dto.response.RegionNewsResponse;
import com.hackathon.junglegym.domain.regionNews.entity.NewsCategory;
import com.hackathon.junglegym.domain.regionNews.entity.RegionNews;
import com.hackathon.junglegym.domain.regionNews.mapper.RegionNewsMapper;
import com.hackathon.junglegym.domain.regionNews.repository.RegionNewsRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionNewsService {

  private final RegionRepository regionRepository;
  private final RegionNewsRepository regionNewsRepository;
  private final OpenAiNewsService ai;
  private final MediaOrientationRepository mediaOrientationRepository;

  private final GoogleNewsRssCrawler google = new GoogleNewsRssCrawler();

  /** 화이트리스트 언론사 */
  private static final Set<String> PRIMARY_SOURCES = Set.of("시민일보", "연합뉴스", "헤럴드경제", "네이트", "세계일보");

  // ===== 주간 범위 계산 =====
  public static LocalDate mondayOfLastWeek(LocalDate today) {
    LocalDate thisMonday = today.with(DayOfWeek.MONDAY);
    return thisMonday.minusWeeks(1);
  }

  public static LocalDate sundayOfLastWeek(LocalDate today) {
    return mondayOfLastWeek(today).plusDays(6);
  }

  // ====== 조회 API ======
  @Transactional
  public List<RegionNewsResponse> getWeeklyNews(Long regionId) {
    Region region = regionRepository.findById(regionId).orElseThrow();
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    LocalDate start = mondayOfLastWeek(today);
    LocalDate end = sundayOfLastWeek(today);

    List<RegionNews> rows =
        regionNewsRepository.findByRegionAndDateBetweenOrderByCreatedAtDesc(region, start, end);
    return rows.stream().map(RegionNewsMapper::toResponse).collect(Collectors.toList());
  }

  /** 지역명으로 목록 조회 (요청 지역 + '서울시') */
  @Transactional(readOnly = true)
  public List<RegionNewsResponse> getWeeklyNewsByRegionName(String regionName) {
    String name = regionName == null ? "" : regionName.trim();

    // 1) 지역 검증 ("서울시"는 예외적으로 바로 허용)
    if (!"서울시".equals(name)) {
      regionRepository
          .findByName(name)
          .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));
    }

    // 2) 조회 기간: 지난주(월~일)
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    LocalDate start = mondayOfLastWeek(today);
    LocalDate end = sundayOfLastWeek(today);

    // 3) 요청 지역 + "서울시" 뉴스 목록 최신순
    List<String> regionNames = "서울시".equals(name) ? List.of("서울시") : List.of(name, "서울시");

    List<RegionNews> rows =
        regionNewsRepository.findByRegion_NameInAndDateBetweenOrderByCreatedAtDesc(
            regionNames, start, end);

    // 4) DTO 변환
    return rows.stream().map(RegionNewsMapper::toResponse).toList();
  }

  // ====== 전체 수동/스케줄 동기화 ======
  public int syncWeeklyAllRegions() {
    List<Region> regions = regionRepository.findAll();
    int total = 0;
    for (Region r : regions) {
      try {
        total += syncWeeklyForRegion(r.getId()); // 지역별 트랜잭션 분리
        Thread.sleep(200);
      } catch (Exception e) {
        log.warn("[REGION-NEWS] weekly sync fail regionId={}", r.getId(), e);
      }
    }
    log.info("[REGION-NEWS] weekly sync done. totalSaved={}", total);
    return total;
  }

  // ====== 지역별 동기화(지난주 → 5건 선택) ======
  @Transactional
  public int syncWeeklyForRegion(Long regionId) throws Exception {
    Region region = regionRepository.findById(regionId).orElseThrow();

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    LocalDate start = mondayOfLastWeek(today);
    LocalDate end = sundayOfLastWeek(today);

    // ✅ 지역별 건수 카운터
    AtomicInteger savedCount = new AtomicInteger(0);

    // 기존 주간 데이터 소프트 삭제
    List<RegionNews> old =
        regionNewsRepository.findByRegionAndDateBetweenOrderByCreatedAtDesc(region, start, end);
    if (!old.isEmpty()) {
      regionNewsRepository.deleteAll(old);
    }

    // ===== Phase 1: 화이트리스트 매체만 허용해서 빠르게 모으기 =====
    List<NewsItem> phase1 = fetchAndFilter(region, start, end, /* allowSecondary= */ false);

    // 조기 종료(5개 확보 시)
    List<NewsItem> collected = new ArrayList<>(phase1);
    if (collected.size() < 5) {
      // ===== Phase 2: 비화이트리스트까지 허용해서 보강 =====
      List<NewsItem> phase2 = fetchAndFilter(region, start, end, /* allowSecondary= */ true);
      // 중복 제거
      for (NewsItem n : phase2) {
        if (collected.size() >= 12) {
          break; // 과도한 후보는 불필요
        }
        boolean dup =
            collected.stream()
                .anyMatch(m -> jaccard(normalize(n.title()), normalize(m.title())) >= 0.72);
        if (!dup) {
          collected.add(n);
        }
        if (collected.size() >= 5) {
          break; // 5개면 충분
        }
      }
    }

    // 최신순 정렬
    collected.sort(
        Comparator.comparing((NewsItem x) -> x.publishedAt() != null ? x.publishedAt() : nvlDate())
            .reversed());
    List<NewsItem> pick = collected.stream().limit(5).toList();

    // ✅ 후보가 없으면 즉시 종료 (스레드풀 생성 금지)
    if (pick.isEmpty()) {
      log.info(
          "[REGION-NEWS] {}({}) weekly saved=0, period={}~{} (no candidates)",
          region.getName(),
          region.getId(),
          start,
          end);
      return 0;
    }

    // ===== OpenAI 분석 + 저장: 병렬 처리 =====
    var pool = java.util.concurrent.Executors.newFixedThreadPool(Math.min(6, pick.size()));
    try {
      List<java.util.concurrent.CompletableFuture<Void>> tasks = new ArrayList<>();

      for (NewsItem n : pick) {
        tasks.add(
            java.util.concurrent.CompletableFuture.runAsync(
                () -> {
                  try {
                    String rawTitle = Objects.toString(n.title(), "");
                    String title = Jsoup.parse(rawTitle).text();
                    String snip = Objects.toString(n.snippet(), "");

                    // 1회 호출로 분석
                    var a = ai.analyzeArticle(title, snip);
                    if (!a.isCivicInfo()) {
                      return; // 정보성 아니면 스킵
                    }
                    // 1) 언론사명 추출
                    String mediaName = extractMediaName(n, title);

                    // 2) 제목 꼬리표 제거 (저장용)
                    String cleanTitle = stripMediaSuffixFromTitle(title);

                    // 3) MediaOrientation 조회(있으면 FK 세팅)
                    MediaOrientation mo = null;
                    if (mediaName != null && !mediaName.isBlank()) {
                      mo = mediaOrientationRepository.findById(mediaName.trim()).orElse(null);
                    }

                    String catStr = a.getCategory();
                    RegionNews row =
                        RegionNews.builder()
                            .region(region)
                            .category(mapCategory(catStr))
                            .title(cut(cleanTitle, 255)) // ✅ 언론사 꼬리표 제거된 제목 저장
                            .oneLineContent(cut(a.getOneLine(), 255))
                            .summary(a.getSummary())
                            .link(normalizeGoogleLink(n.link()))
                            .date(n.publishedAt() != null ? n.publishedAt().toLocalDate() : start)
                            .mediaOrientation(mo) // FK (있으면)
                            .mediaName(cut(mediaName, 100)) // 텍스트도 항상 저장 (없으면 null)
                            .build();

                    regionNewsRepository.save(row);
                    savedCount.incrementAndGet(); // ✅ 저장 성공 시 즉시 증가
                  } catch (Exception ex) {
                    log.warn("[REGION-NEWS] save skip (title={})", n.title(), ex);
                  }
                },
                pool));
      }

      // 전부 완료 대기 (최대 20초 제한 등 방어)
      java.util.concurrent.CompletableFuture.allOf(
              tasks.toArray(new java.util.concurrent.CompletableFuture[0]))
          .get(
              java.time.Duration.ofSeconds(20).toMillis(),
              java.util.concurrent.TimeUnit.MILLISECONDS);

    } catch (Exception waitEx) {
      log.warn("[REGION-NEWS] parallel analyze/save wait interrupted", waitEx);
    } finally {
      pool.shutdownNow();
    }

    int saved = savedCount.get(); // ✅ 최종 카운트
    log.info(
        "[REGION-NEWS] {}({}) weekly saved={}, period={}~{}",
        region.getName(),
        region.getId(),
        saved,
        start,
        end);

    return saved;
  }

  private String buildRegionQuery(Region r) {
    List<String> tokens = new ArrayList<>();
    tokens.add("\"" + r.getName().trim() + "\"");
    if (r.getName() != null) {
      for (String t : r.getName().split("[,\\s]+")) {
        if (t.trim().length() >= 2) {
          tokens.add("\"" + t.trim() + "\"");
        }
      }
    }
    return String.join(" OR ", tokens);
  }

  private String normalize(String s) {
    String t = Jsoup.parse(Objects.toString(s, "")).text().toLowerCase(java.util.Locale.ROOT);
    return t.replaceAll("[^가-힣a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
  }

  private List<NewsItem> dedupByTopic(List<NewsItem> items) {
    List<NewsItem> out = new ArrayList<>();
    for (NewsItem n : items) {
      String a = normalize(n.title());
      boolean dup = false;
      for (NewsItem m : out) {
        String b = normalize(m.title());
        if (jaccard(a, b) >= 0.72) {
          dup = true;
          break;
        }
      }
      if (!dup) {
        out.add(n);
      }
    }
    return out;
  }

  private double jaccard(String a, String b) {
    Set<String> A = new HashSet<>(Arrays.asList(a.split(" ")));
    Set<String> B = new HashSet<>(Arrays.asList(b.split(" ")));
    A.removeIf(x -> x.length() < 2);
    B.removeIf(x -> x.length() < 2);
    if (A.isEmpty() || B.isEmpty()) {
      return 0;
    }
    Set<String> inter = new HashSet<>(A);
    inter.retainAll(B);
    Set<String> union = new HashSet<>(A);
    union.addAll(B);
    return inter.size() * 1.0 / union.size();
  }

  private NewsCategory mapCategory(String ai) {
    String k = ai == null ? "" : ai.replace(" ", "");
    switch (k) {
      case "정치":
        return NewsCategory.POLITICS;
      case "경제":
        return NewsCategory.ECONOMY;
      case "세계":
        return NewsCategory.WORLD;
      case "생활/문화":
      case "생활문화":
        return NewsCategory.LIFE_CULTURE;
      case "IT/과학":
      case "IT과학":
        return NewsCategory.IT_SCIENCE;
      default:
        return NewsCategory.SOCIETY;
    }
  }

  private static java.time.ZonedDateTime nvlDate() {
    return java.time.ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusYears(50);
  }

  // 구글 RSS 리디렉트 링크에서 원문 url= 추출
  private String normalizeGoogleLink(String link) {
    if (link == null) {
      return null;
    }
    try {
      int i = link.indexOf("url=");
      if (i >= 0) {
        String tail = link.substring(i + 4);
        int amp = tail.indexOf('&');
        String raw = (amp >= 0 ? tail.substring(0, amp) : tail);
        return java.net.URLDecoder.decode(raw, java.nio.charset.StandardCharsets.UTF_8);
      }
    } catch (Exception ignored) {
    }
    return link;
  }

  private String cut(String s, int max) {
    if (s == null) {
      return null;
    }
    return s.length() <= max ? s : s.substring(0, max);
  }

  /** 한 번의 fetch로 필요한 만큼만 빠르게 모아오고, 조기 종료/전처리까지 수행 */
  private List<NewsItem> fetchAndFilter(
      Region region, LocalDate start, LocalDate end, boolean allowSecondary) throws Exception {
    String query = buildRegionQuery(region);

    // 1차는 더 적게 가져와도 됨(속도). 2차라면 좀 더 넉넉히.
    int fetchSize = allowSecondary ? 120 : 60;
    List<NewsItem> raw = google.fetch(query, fetchSize);

    List<NewsItem> out = new ArrayList<>();
    Set<String> seenTitles = new HashSet<>();

    for (NewsItem n : raw) {
      if (n.publishedAt() == null) {
        continue;
      }
      LocalDate d = n.publishedAt().toLocalDate();
      if (d.isBefore(start) || d.isAfter(end)) {
        continue;
      }

      // 소스 필터
      String src = Objects.toString(n.source(), "").trim();

      // 제목은 먼저 뽑아둔다 (꼬리표 추출/비교에 사용)
      String rawTitle = Objects.toString(n.title(), "");
      String titleText = Jsoup.parse(rawTitle).text();
      // boolean srcOk = allowSecondary ? (src.length() > 0) : sourceMatchesPrimary(src); // 기존
      // 교체:
      boolean srcOk =
          allowSecondary
              ? (src.length() > 0 || (extractMediaFromTitle(titleText) != null))
              : (sourceMatchesPrimary(src)
                  || sourceMatchesPrimary(extractMediaFromTitle(titleText)));
      if (!srcOk) {
        continue;
      }

      String title = Jsoup.parse(Objects.toString(n.title(), "")).text();
      String snip = Objects.toString(n.snippet(), "");

      // 아주 초벌 중복 제거(제목 기반)
      String norm = normalize(title);
      if (!seenTitles.add(norm)) {
        continue;
      }

      // 너무 길게 모으지 말고, 충분하면 조기 종료
      out.add(n);
      if (out.size() >= 12) {
        break;
      }
    }

    // 토픽 유사 중복 제거
    out = dedupByTopic(out);

    // 충분히 모였으면 바로 반환 (조기 종료)
    if (out.size() >= 5) {
      return out;
    }

    // 부족해도 일단 있는 만큼 반환(2차에서 보강)
    return out;
  }

  private boolean sourceMatchesPrimary(String source) {
    if (source == null) {
      return false;
    }
    String s = source.trim();
    if (s.isEmpty()) {
      return false;
    }
    if (PRIMARY_SOURCES.contains(s)) {
      return true;
    }
    return PRIMARY_SOURCES.stream().anyMatch(w -> s.startsWith(w) || s.contains(w));
  }

  // ====== 스케줄러 ======

  /** 매주 월요일 02:00 KST, 지난주(월~일) 데이터 수집/저장 */
  @Scheduled(cron = "0 0 2 * * MON", zone = "Asia/Seoul")
  public void scheduledWeeklySync() {
    int total = syncWeeklyAllRegions();
    log.info("[REGION-NEWS] weekly scheduled sync done, totalSaved={}", total);
  }

  /** 언론사명 추출: source() 우선, 없으면 제목 꼬리표에서 추출 */
  private String extractMediaName(NewsItem n, String title) {
    // 1) RSS의 source 우선
    String src = Objects.toString(n.source(), "").trim();
    if (!src.isBlank()) {
      return cleanMediaName(src);
    }
    // 2) 제목에서 추출
    return cleanMediaName(extractMediaFromTitle(title));
  }

  /** 제목 끝의 " - 언론사" 또는 유사 패턴에서 언론사명 추출 */
  private String extractMediaFromTitle(String title) {
    if (title == null) {
      return null;
    }
    String t = Jsoup.parse(title).text();

    // 공통 하이픈/대시들 통일: " - "로 치환
    String norm = t.replaceAll("\\s*[–—-]\\s*", " - ");

    // 케이스1: 마지막 " - 언론사"
    int dash = norm.lastIndexOf(" - ");
    if (dash >= 0 && dash + 3 < norm.length()) {
      String tail = norm.substring(dash + 3).trim();
      if (!tail.isBlank()) {
        return tail;
      }
    }

    // 케이스2: "> 뉴스 - 언론사"
    String marker = "> 뉴스 - ";
    int k = norm.lastIndexOf(marker);
    if (k >= 0 && k + marker.length() < norm.length()) {
      String tail = norm.substring(k + marker.length()).trim();
      if (!tail.isBlank()) {
        return tail;
      }
    }

    return null;
  }

  /** 저장용: 제목에서 언론사 꼬리표 제거 */
  private String stripMediaSuffixFromTitle(String title) {
    if (title == null) {
      return null;
    }
    String t = Jsoup.parse(title).text();
    String norm = t.replaceAll("\\s*[–—-]\\s*", " - ");

    // "> 뉴스 - 언론사" 꼬리표 제거 우선
    String marker = "> 뉴스 - ";
    int m = norm.lastIndexOf(marker);
    if (m >= 0) {
      // 앞부분만 유지
      return norm.substring(0, m).trim();
    }

    // 마지막 " - 언론사" 제거
    int dash = norm.lastIndexOf(" - ");
    if (dash >= 0) {
      return norm.substring(0, dash).trim();
    }

    return t.trim(); // 변경 없음
  }

  /** PK 매칭 안정화(공백만 정리. 과도한 변환 금지) */
  private String cleanMediaName(String name) {
    if (name == null) {
      return null;
    }
    String s = name.replaceAll("[\\u00A0\\p{Zs}]+", " ").trim();
    return s.isEmpty() ? null : s;
  }
}
