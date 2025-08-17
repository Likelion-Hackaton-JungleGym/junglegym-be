package com.hackathon.junglegym.domain.politicianIssue.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class OpenAiNewsService {

  private final OkHttpClient http =
      new OkHttpClient.Builder()
          .callTimeout(Duration.ofSeconds(30))
          .readTimeout(Duration.ofSeconds(25))
          .connectTimeout(Duration.ofSeconds(10))
          .build();
  private final ObjectMapper om = new ObjectMapper();

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.model:gpt-4o-mini}")
  private String model;

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  // 추가 메서드

  /** 제목/요약이 '정치 관련 기사'인지 판별: Y/N만 반환 */
  public boolean isPolitical(String title, String snippet) throws Exception {
    String system =
        "당신은 한국어 뉴스 분류기입니다. 정당/의회/정부/선거/정책/법안 뿐 아니라, "
            + "특정 정치인 관련 수사·사건·논란·발언·일정·지역구 이슈, 정치인 인터뷰·칼럼도 정치 관련으로 간주합니다. "
            + "정치 관련이면 'Y', 아니면 'N'만 출력.";
    // "당신은 한국어 뉴스 분류기입니다. 입력이 정치 관련 기사(정당, 의회, 선거, 정부/지자체, 정책/법안, 공직자 논란, 정치인 인터뷰·칼럼 포함)라면 'Y', 아니면
    // 'N'만 출력하세요.";
    String user = "제목: " + safe(title) + "\n요약/문단: " + safe(snippet) + "\n응답: Y 또는 N";
    String out = chat(system, user, 0.0, 8).trim().toUpperCase();
    return out.startsWith("Y");
  }

  private String chat(String system, String user, double temp, int maxTokens) throws Exception {
    Map<String, Object> payload =
        Map.of(
            "model",
            model,
            "temperature",
            temp,
            "max_tokens",
            maxTokens,
            "messages",
            List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", user)));
    RequestBody body = RequestBody.create(om.writeValueAsString(payload), JSON);
    Request req =
        new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();

    try (Response resp = http.newCall(req).execute()) {
      if (!resp.isSuccessful() || resp.body() == null) {
        throw new RuntimeException("OpenAI fail: " + resp.code());
      }
      JsonNode root = om.readTree(resp.body().string());
      return root.at("/choices/0/message/content").asText("");
    }
  }

  private String safe(String s) {
    return s == null ? "" : (s.length() > 1500 ? s.substring(0, 1500) : s);
  }

  //  /** 편향/감정/추측 어투가 적고 팩트 중심이면 0~100 점수 리턴 */
  //  public int objectivityScore(String politicianName, String title, String snippet)
  //      throws Exception {
  //    String system =
  //        "다음 한국어 기사 요약/제목을 보고 ‘팩트 중심성’을 0~100으로 채점합니다. "
  //            + "원칙: (1) 감정/평가/선정적 어휘가 적을수록 가점 (2) 의혹/주장만 있고 근거 없는 경우 감점 (3) 개인 비방성 강하면 감점. "
  //            + "정수만 출력.";
  //    String user =
  //        "정치인: "
  //            + politicianName
  //            + "\n제목: "
  //            + safe(title)
  //            + "\n요약/문단: "
  //            + safe(snippet)
  //            + "\n응답형식: 숫자만 (예: 73)";
  //    String score = chat(system, user, 0.4, 64).trim();
  //    try {
  //      return Math.max(0, Math.min(100, Integer.parseInt(score.replaceAll("[^0-9]", ""))));
  //    } catch (Exception e) {
  //      return 0;
  //    }
  //  }
  //
  //  /** 제목 아님! ‘핵심 한 줄(띄어쓰기 포함 30~35자)’ */
  //  public String oneLineSummary(String politicianName, String title, String snippet)
  //      throws Exception {
  //    String system =
  //        "정치 기사 핵심만 뽑아 ‘한 줄’로 압축하는 한국어 비서. " + "길이: 띄어쓰기 포함 30~35자. 과장/평가는 금지. 주어는 가능하면 해당 정치인.";
  //    String user =
  //        "정치인: "
  //            + politicianName
  //            + "\n원제목: "
  //            + safe(title)
  //            + "\n본문요약/문단: "
  //            + safe(snippet)
  //            + "\n출력은 한 줄만, 마침표 없이.";
  //    return chat(system, user, 0.2, 64).replace("\n", " ").trim();
  //  }
}
