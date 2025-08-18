package com.hackathon.junglegym.domain.activity.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.activity.entity.Activity;
import com.hackathon.junglegym.domain.activity.repository.ActivityRepository;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.politicianIssue.dto.NewsItem;
import com.hackathon.junglegym.domain.politicianIssue.service.GoogleNewsRssCrawler;
import com.hackathon.junglegym.domain.politicianIssue.service.OpenAiNewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivitySyncService {

  private final PoliticianRepository politicianRepository;
  private final ActivityRepository activityRepository;

  private final GoogleNewsRssCrawler google = new GoogleNewsRssCrawler();
  private final OpenAiNewsService ai;

  // ---------- 파라미터/사전 ----------

  private static final int RECENT_DAYS = 30; // 활동 기사는 최근 30일
  private static final int FETCH_LIMIT = 100; // RSS 최대 수집 수
  private static final int PICK_COUNT = 3; // 최종 저장 수
  private static final int MAX_AI_CHECK = 10; // AI 확인 상한

  // 제외(비활동) 트리거
  private static final Set<String> EXCLUDE_HINTS =
      Set.of(
          "의혹", "수사", "기소", "고발", "고소", "체포", "논란", "비판", "맹비난", "발언", "인터뷰", "라디오", "방송", "출연",
          "칼럼", "사설", "오피니언", "sns", "페북", "인스타", "트위터", "x ", "유튜브", "동영상", "예고", "참석 예정");

  // 활동(포함) 트리거 — 점수 가산
  private static final Set<String> ACTION_VERBS =
      Set.of(
          "발의", "상정", "통과", "의결", "공표", "공고", "시행", "발표", "착공", "준공", "개소", "개관", "설치", "점검", "단속",
          "확보", "배정", "지급", "유치", "개정", "추진", "체결", "개최");
  private static final Set<String> ACTION_NOUNS =
      Set.of("법안", "예산", "조례", "조치", "공사", "사업", "합의", "협약", "정책", "계획안", "대책", "성과", "통계", "보도자료");
  private static final Set<String> QUANT_UNITS =
      Set.of("억원", "%", "명", "가구", "건", "km", "톤", "회", "대", "개");

  // ---------- 내부 모델 (final + extraScore) ----------

  static class Scored {

    final NewsItem n; // 기사
    final int baseScore; // 휴리스틱 기본점수(불변)
    int extraScore = 0; // 가산점(가변)

    Scored(NewsItem n, int baseScore) {
      this.n = n;
      this.baseScore = baseScore;
    }

    int getTotalScore() {
      return baseScore + extraScore;
    }
  }

  // ---------- Public ----------

  /** 활동 관련 기사 Top-N 저장 (중복/유사 제거) */
  @Transactional
  public int syncActivities(Long politicianId) throws Exception {
    Politician p = politicianRepository.findById(politicianId).orElseThrow();
    String query = buildQuery(p);

    List<NewsItem> raw = google.fetch(query, FETCH_LIMIT);
    log.debug("[ACT] fetched={} (q={})", raw.size(), query);

    // 최근, 인물매칭, 기본 점수 부여
    List<Scored> pool = new ArrayList<>();
    for (NewsItem n : raw) {
      if (!withinRecentDays(n.publishedAt(), RECENT_DAYS)) {
        continue;
      }
      if (!matchPersonLoose(n, p)) {
        continue;
      }

      int base = baseScore(n); // 포함/제외 사전으로 빠르게 점수
      if (base < 0) {
        continue; // 강한 제외 신호 => 탈락
      }

      pool.add(new Scored(n, base));
    }

    // AI 판별은 애매한 후보 중 상한 내에서만
    int budget = MAX_AI_CHECK;
    for (Scored s : pool) {
      if (budget <= 0) {
        break;
      }
      // 이미 높은 점수면 AI 생략
      if (s.baseScore >= 60) {
        continue;
      }

      try {
        if (ai.isActivity(s.n.title(), s.n.snippet())) {
          s.extraScore += 50; // 가산점
        } else {
          s.extraScore -= 40; // 패널티
        }
      } catch (Exception e) {
        log.warn("[ACT] isActivity fail: {}", s.n.link(), e);
      } finally {
        budget--;
      }
    }

    // 정렬 → 제목/토픽 유사 제거 → 상위 N
    pool.sort((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()));
    List<Scored> deduped = dedupByTitleAndTopic(pool);
    List<Scored> picked = deduped.stream().limit(PICK_COUNT).toList();

    // 저장(upsert by link)
    int saved = 0;
    for (Scored s : picked) {
      String title = Jsoup.parse(Objects.toString(s.n.title(), "")).text();
      Activity entity = activityRepository.findByPoliticianAndLink(p, s.n.link()).orElse(null);
      if (entity == null) {
        entity = Activity.builder().politician(p).title(title).link(s.n.link()).build();
      } else {
        entity =
            Activity.builder()
                .id(entity.getId())
                .politician(p)
                .title(title)
                .link(s.n.link())
                .isDeleted(false)
                .build();
      }
      activityRepository.save(entity);
      saved++;
    }

    log.info(
        "[ACT] {} -> fetched={}, pool={}, picked={}, saved={}",
        p.getName(),
        raw.size(),
        pool.size(),
        picked.size(),
        saved);
    return saved;
  }

  // ---------- 점수/휴리스틱 ----------

  private int baseScore(NewsItem n) {
    String title = safe(Jsoup.parse(Objects.toString(n.title(), "")).text());
    String body = safe(Objects.toString(n.snippet(), ""));
    String joined = (title + " " + body).toLowerCase(Locale.ROOT);

    // 강한 제외 신호
    for (String x : EXCLUDE_HINTS) {
      if (joined.contains(x.toLowerCase(Locale.ROOT))) {
        return -999;
      }
    }

    int score = 0;

    // 활동 트리거 가산
    for (String v : ACTION_VERBS) {
      if (joined.contains(v.toLowerCase(Locale.ROOT))) {
        score += 20;
      }
    }
    for (String w : ACTION_NOUNS) {
      if (joined.contains(w.toLowerCase(Locale.ROOT))) {
        score += 15;
      }
    }

    // 숫자/단위 신호
    if (containsQuantity(joined)) {
      score += 10;
    }

    // 출처 가중(정부/공공기관일수록 +) — 필요시 도메인 기반 가산 추가 가능
    return score;
  }

  private boolean containsQuantity(String text) {
    boolean hasDigit = text.chars().anyMatch(Character::isDigit);
    if (!hasDigit) {
      return false;
    }
    for (String u : QUANT_UNITS) {
      if (text.contains(u.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }
    return false;
  }

  // ---------- 유사/중복 제거 ----------

  private List<Scored> dedupByTitleAndTopic(List<Scored> list) {
    List<Scored> out = new ArrayList<>();
    Set<String> seenTitle = new HashSet<>();

    for (Scored s : list) {
      String norm = normalizeTitle(s.n.title());
      if (seenTitle.contains(norm)) {
        continue;
      }

      boolean dupTopic = false;
      for (Scored t : out) {
        if (topicSim(norm, normalizeTitle(t.n.title())) >= 0.72) {
          dupTopic = true;
          break;
        }
      }
      if (!dupTopic) {
        seenTitle.add(norm);
        out.add(s);
      }
    }
    return out;
  }

  private String normalizeTitle(String title) {
    String t = Jsoup.parse(Objects.toString(title, "")).text().toLowerCase(Locale.ROOT);
    return t.replaceAll("[^가-힣a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
  }

  private double topicSim(String a, String b) {
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

  // ---------- 공통 유틸 ----------

  private boolean withinRecentDays(ZonedDateTime zdt, int days) {
    if (zdt == null) {
      return false;
    }
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    long d = ChronoUnit.DAYS.between(zdt.toLocalDate(), now.toLocalDate());
    return d >= 0 && d <= days;
  }

  private boolean matchPersonLoose(NewsItem n, Politician p) {
    String title = Jsoup.parse(Objects.toString(n.title(), "")).text();
    String snip = Objects.toString(n.snippet(), "");
    String joined = (title + " " + snip).toLowerCase(Locale.ROOT);

    if (containsName(joined, p.getName())) {
      return true;
    }

    int hits = 0;
    if (notBlank(p.getPolyName()) && joined.contains(p.getPolyName().toLowerCase(Locale.ROOT))) {
      hits++;
    }
    if (notBlank(p.getCommittee()) && joined.contains(p.getCommittee().toLowerCase(Locale.ROOT))) {
      hits++;
    }
    if (notBlank(p.getRoleName()) && joined.contains(p.getRoleName().toLowerCase(Locale.ROOT))) {
      hits++;
    }
    if (notBlank(p.getRegionText())) {
      for (String t : p.getRegionText().split("[,\\s]+")) {
        t = t.trim();
        if (t.length() >= 2 && joined.contains(t.toLowerCase(Locale.ROOT))) {
          hits++;
          break;
        }
      }
    }
    return hits >= 2;
  }

  private boolean containsName(String joinedLower, String personName) {
    String name = Objects.toString(personName, "").trim().toLowerCase(Locale.ROOT);
    if (name.isBlank()) {
      return false;
    }
    if (joinedLower.contains(name)) {
      return true;
    }
    return joinedLower.replace(" ", "").contains(name.replace(" ", ""));
  }

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
    return "\"" + name + "\"" + extras;
  }

  private boolean notBlank(String s) {
    return s != null && !s.isBlank();
  }

  private String safe(String s) {
    return s == null ? "" : s;
  }
}
