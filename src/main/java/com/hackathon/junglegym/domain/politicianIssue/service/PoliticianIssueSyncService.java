package com.hackathon.junglegym.domain.politicianIssue.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.politicianIssue.dto.NewsItem;
import com.hackathon.junglegym.domain.politicianIssue.entity.PoliticianIssue;
import com.hackathon.junglegym.domain.politicianIssue.repository.PoliticianIssueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliticianIssueSyncService {

  private final PoliticianRepository politicianRepository;
  private final PoliticianIssueRepository issueRepository;
  private final OpenAiNewsService ai;

  // 데이터 수집: Google 뉴스 RSS
  private final GoogleNewsRssCrawler google = new GoogleNewsRssCrawler();

  private static final Set<String> OPINION_WORDS = Set.of("칼럼", "사설", "기고", "오피니언");

  // 튜닝 포인트
  private static final int RECENT_DAYS = 20;
  private static final int PICK_COUNT = 3;
  private static final int FETCH_LIMIT = 100;
  private static final int MAX_AI_CHECK = 20; // 애매한 것만 AI 확인

  private static final Set<String> POLITICS_HINTS =
      Set.of(
          "국회", "의원", "국회의원", "상임위", "법안", "입법", "정책", "선거", "총선", "보궐", "공천", "출마", "지지율", "장관",
          "차관", "시장", "청장", "구청장", "도지사", "군수", "청와대", "대통령실", "국무회의", "정부", "더불어민주당", "민주당",
          "국민의힘", "국힘", "정의당", "녹색당", "진보당", "개혁신당", "새로운미래", "원내대표", "당대표", "칼럼");

  @Transactional
  public int syncIssues(Long politicianId) throws Exception {
    Politician p = politicianRepository.findById(politicianId).orElseThrow();
    String name = p.getName() == null ? "" : p.getName().trim();
    if (name.isBlank()) {
      log.info("[ISSUE] skip: empty name politicianId={}", politicianId);
      return 0;
    }

    long t0 = System.currentTimeMillis();

    // 1) 수집 (정치인 중심 쿼리)
    String query = buildQuery(p);
    List<NewsItem> raw = google.fetch(query, FETCH_LIMIT);

    // 최신순 정렬(발행일 없는 건 뒤로)
    raw.sort(
        Comparator.comparing(
                (NewsItem n) ->
                    Optional.ofNullable(n.publishedAt())
                        .orElse(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"))))
            .reversed());

    // 2) 1차 필터(빠름): 인물 매칭 + 최근 N일
    List<NewsItem> hardPass = new ArrayList<>();
    List<NewsItem> maybe = new ArrayList<>();
    for (NewsItem n : raw) {
      if (!withinRecentDays(n.publishedAt(), RECENT_DAYS)) {
        continue;
      }
      if (!matchPersonLoose(n, p)) {
        continue;
      }

      if (hasPoliticsHints(n)) {
        hardPass.add(n);
      } else {
        maybe.add(n);
      }
    }

    // 3) 2차 필터(AI: 애매한 것만 상한 내 확인)
    int aiBudget = MAX_AI_CHECK;
    List<NewsItem> aiPass = new ArrayList<>();
    for (NewsItem n : maybe) {
      if (aiBudget <= 0) {
        break;
      }
      try {
        if (ai.isPolitical(n.title(), n.snippet())) {
          aiPass.add(n);
        }
      } catch (Exception e) {
        log.warn("[ISSUE] isPolitical() fail, skip link={}", n.link(), e);
      } finally {
        aiBudget--;
      }
    }

    // 4) 합치고 제목 중복 제거 → 상위 3건
    List<NewsItem> merged = Stream.concat(hardPass.stream(), aiPass.stream()).toList();
    List<NewsItem> deduped = dedupByTitle(merged);
    List<NewsItem> picked = deduped.stream().limit(PICK_COUNT).toList();

    // 5) fallback: 그래도 부족하면 이름 매칭+최근으로 보충
    if (picked.size() < PICK_COUNT) {
      var fallback =
          raw.stream()
              .filter(n -> withinRecentDays(n.publishedAt(), RECENT_DAYS))
              .filter(n -> titleContainsName(n.title(), name))
              .filter(n -> passPolitics(n, p))
              .filter(n -> deduped.stream().noneMatch(x -> Objects.equals(x.link(), n.link())))
              .limit(PICK_COUNT - picked.size())
              .toList();
      picked = Stream.concat(picked.stream(), fallback.stream()).limit(PICK_COUNT).toList();
    }

    // 6) 저장 (제목 그대로 저장)
    int saved = 0;
    for (NewsItem n : picked) {
      String titlePlain = Jsoup.parse(Objects.toString(n.title(), "")).text();
      PoliticianIssue entity = issueRepository.findByPoliticianAndLink(p, n.link()).orElse(null);
      if (entity == null) {
        entity = PoliticianIssue.builder().politician(p).title(titlePlain).link(n.link()).build();
      } else {
        entity =
            PoliticianIssue.builder()
                .id(entity.getId())
                .politician(p)
                .title(titlePlain)
                .link(n.link())
                .isDeleted(false)
                .build();
      }
      issueRepository.save(entity);
      saved++;
    }

    long ms = System.currentTimeMillis() - t0;
    log.info(
        "[ISSUE] {} -> fetched={}, hardPass={}, aiPass={}, saved={}, {}ms (q={})",
        name,
        raw.size(),
        hardPass.size(),
        aiPass.size(),
        saved,
        ms,
        query);
    return saved;
  }

  // ===== Helper =====

  /** 구글 뉴스 RSS 검색 쿼리 생성: 이름을 인용부호로 정확히, 보조 신호는 OR 로 가볍게 */
  private String buildQuery(Politician p) {
    String name = Objects.toString(p.getName(), "").trim();
    List<String> tokens = new ArrayList<>();
    if (notBlank(p.getPolyName())) {
      tokens.add("\"" + p.getPolyName().trim() + "\"");
    }
    if (notBlank(p.getRoleName())) {
      tokens.add("\"" + p.getRoleName().trim() + "\"");
    }
    if (notBlank(p.getCommittee())) {
      tokens.add("\"" + p.getCommittee().trim() + "\"");
    }
    if (notBlank(p.getRegionText())) {
      for (String t : p.getRegionText().split("[,\\s]+")) {
        if (t.trim().length() >= 2) {
          tokens.add("\"" + t.trim() + "\"");
        }
      }
    }
    String extras = tokens.isEmpty() ? "" : " (" + String.join(" OR ", tokens) + ")";
    // 이름은 정확히 일치시키기 위해 쌍따옴표
    return "\"" + name + "\"" + extras;
  }

  /** 제목/요약에 정치 키워드 포함 여부(간단 휴리스틱) */
  private boolean hasPoliticsHints(NewsItem n) {
    String joined =
        safeLower(
            Jsoup.parse(Objects.toString(n.title(), "")).text()
                + " "
                + Objects.toString(n.snippet(), ""));
    for (String k : POLITICS_HINTS) {
      if (joined.contains(safeLower(k))) {
        return true;
      }
    }
    return false;
  }

  /** 인물 매칭(이름 또는 보조신호 2개 이상) */
  private boolean matchPersonLoose(NewsItem n, Politician p) {
    String title = Jsoup.parse(Objects.toString(n.title(), "")).text();
    String snip = Objects.toString(n.snippet(), "");
    String joined = safeLower(title + " " + snip);

    if (containsName(joined, p.getName())) {
      return true;
    }

    int hits = 0;
    if (notBlank(p.getPolyName()) && joined.contains(safeLower(p.getPolyName()))) {
      hits++;
    }
    if (notBlank(p.getCommittee()) && joined.contains(safeLower(p.getCommittee()))) {
      hits++;
    }
    if (notBlank(p.getRoleName()) && joined.contains(safeLower(p.getRoleName()))) {
      hits++;
    }
    if (notBlank(p.getRegionText())) {
      for (String t : p.getRegionText().split("[,\\s]+")) {
        t = t.trim();
        if (t.length() >= 2 && joined.contains(safeLower(t))) {
          hits++;
          break;
        }
      }
    }
    return hits >= 2;
  }

  /** 이름(공백 제거 변형 포함) 매칭 */
  private boolean containsName(String joinedLower, String personName) {
    String name = safeLower(Objects.toString(personName, "").trim());
    if (name.isBlank()) {
      return false;
    }
    if (joinedLower.contains(name)) {
      return true;
    }
    return joinedLower.replace(" ", "").contains(name.replace(" ", ""));
  }

  /** 최근 N일 이내만 허용(null이면 통과 X → 지금은 임시로 허용) */
  private boolean withinRecentDays(ZonedDateTime zdt, int days) {
    if (zdt == null) {
      log.debug("[NEWS] drop-by-date? publishedAt=null -> TEMP ALLOW (will be sorted later)");
      return true; // 임시 허용 (혹시 날짜 못 읽은 케이스)
    }
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    long d = ChronoUnit.DAYS.between(zdt.toLocalDate(), now.toLocalDate());
    return d >= 0 && d <= days;
  }

  private List<NewsItem> dedupByTitle(List<NewsItem> list) {
    List<NewsItem> out = new ArrayList<>();
    Set<String> seen = new HashSet<>();
    for (NewsItem n : list) {
      String key =
          safeLower(Jsoup.parse(Objects.toString(n.title(), "")).text()).replaceAll("\\s+", " ");
      if (seen.contains(key)) {
        continue;
      }
      seen.add(key);
      out.add(n);
    }
    return out;
  }

  private String safeLower(String s) {
    return s == null ? "" : s.toLowerCase(Locale.ROOT);
  }

  private boolean notBlank(String s) {
    return s != null && !s.isBlank();
  }

  /** 제목 문자열에 인물명이 포함되는지(공백 제거 변형 포함) */
  private boolean titleContainsName(String title, String personName) {
    String t = safeLower(org.jsoup.Jsoup.parse(Objects.toString(title, "")).text());
    return containsName(t, personName);
  }

  // 스트림에서 호출할 공통 게이트 (칼럼 즉시 통과 → 힌트 포함 → 마지막으로 AI)
  private boolean passPolitics(NewsItem n, Politician p) {
    // passPolitics() 맨 위에 추가
    if (titleContainsName(n.title(), p.getName())) {
      return true;
    }

    String title = n.title() == null ? "" : Jsoup.parse(n.title()).text();
    String snip = n.snippet() == null ? "" : n.snippet();

    // 1) 칼럼/사설 등은 바로 통과 (원치 않으면 false로 바꿔도 됨)
    if (quickOpinionAllow(title)) {
      return true;
    }

    //    // passPolitics() 맨 위에 추가
    //    if (titleContainsName(n.title(), p.getName())) {
    //      return true;
    //    }

    // 2) 키워드 힌트로 빠른 통과(제목/스니펫 둘 다 검사)
    if (containsAny(title, POLITICS_HINTS) || containsAny(snip, POLITICS_HINTS)) {
      return true;
    }

    // 3) 마지막에 AI로 판별
    try {
      return ai.isPolitical(title, snip);
    } catch (Exception e) {
      log.warn("[NEWS] isPolitical failed, allow by fallback: {}", n.link(), e);
      // AI 실패 시 허용(막고 싶으면 false)
      return true;
    }
  }

  // '칼럼' 등 오피니언성 문구가 제목에 있으면 true
  private boolean quickOpinionAllow(String title) {
    String t = title == null ? "" : title;
    for (String w : OPINION_WORDS) {
      if (t.contains(w)) {
        return true;
      }
    }
    return false;
  }

  // 간단 포함 검사(대소문자 구분 없음)
  private boolean containsAny(String text, Set<String> words) {
    if (text == null || text.isBlank()) {
      return false;
    }
    String t = text.toLowerCase(Locale.ROOT);
    for (String w : words) {
      if (t.contains(w.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }
}
