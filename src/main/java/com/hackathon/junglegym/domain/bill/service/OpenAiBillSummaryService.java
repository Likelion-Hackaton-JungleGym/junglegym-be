package com.hackathon.junglegym.domain.bill.service;

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
public class OpenAiBillSummaryService {

  private final OkHttpClient http;
  private final ObjectMapper om = new ObjectMapper();

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.model:gpt-4o-mini}")
  private String model;

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  public OpenAiBillSummaryService() {
    this.http =
        new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(25))
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(20))
            .build();
  }

  /** mainContent를 '1.~4.' 번호 4줄, 각 줄 30~35자로 요약 */
  public String summarizeTo3Lines(String billName, String mainContent) throws Exception {
    if (mainContent == null || mainContent.isBlank()) {
      return null;
    }

    // 안전장치: 요약 입력 너무 길면 앞부분 6,000자만 사용
    String content = mainContent.length() > 6000 ? mainContent.substring(0, 6000) : mainContent;

    String system =
        "당신은 입법안 설명을 간결하게 요약하는 한국어 비서입니다. " + "출력은 정확히 3줄의 불릿으로만 작성하세요. 각 줄은 1문장, 불필요한 수사는 금지합니다.";

    String user =
        "법률안 제목: "
            + (billName == null ? "" : billName)
            + "\n"
            + "제안이유 및 주요내용 전문:\n"
            + content
            + "\n\n"
            + "위 내용을 한국어로 정확히 3줄 불릿(•)로 요약해 주세요. "
            + "각 줄은 핵심만 담고 40자 내외로.";

    // Chat Completions 요청 바디
    Map<String, Object> payload =
        Map.of(
            "model",
            model,
            "temperature",
            0.2,
            "max_tokens",
            240,
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
        throw new RuntimeException("OpenAI 호출 실패: " + resp.code());
      }
      JsonNode root = om.readTree(resp.body().string());
      String out = root.at("/choices/0/message/content").asText();
      // 안전 정리: 공백/줄바꿈 정리 + 3줄만 남김
      String[] lines = out.replace("\r", "").trim().split("\n");
      StringBuilder sb = new StringBuilder();
      int count = 0;
      for (String line : lines) {
        String t = line.trim();
        if (t.isEmpty()) {
          continue;
        }
        // 불릿 없으면 붙여주기
        if (!t.startsWith("•")) {
          t = "• " + t.replaceAll("^[\\-*·]+\\s*", "");
        }
        sb.append(t);
        count++;
        if (count == 3) {
          break;
        }
        sb.append("\n");
      }
      return sb.length() == 0 ? null : sb.toString().trim();
    }
  }
}
