package com.hackathon.junglegym.domain.qeustion.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatCompletionService {

  private final WebClient client =
      WebClient.builder()
          .baseUrl("https://api.openai.com/v1")
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build();

  @Value("${openai.api-key}")
  private String apiKey;

  public String answer(String question, String context, int maxChars) {
    String system =
        String.join(
            "\n",
            "당신은 대한민국 법령 기반 정치 해설 어시스턴트입니다.",
            "반드시 '-습니다' 체로만 답변합니다.",
            "아래 컨텍스트(법 조문)에서 근거를 사용해 답변합니다.",
            "모르면 모른다고 답합니다.",
            "답변은 최대 700자 이내로 합니다.",
            "정치적으로 편향된 해석을 줄 수 있는 답을 제공하지 않습니다. 무조건 중립적으로 답변합니다.");
    String user = "컨텍스트:\n" + context + "\n\n질문:\n" + question;

    Map<String, Object> body =
        Map.of(
            "model",
            "gpt-4o-mini",
            "temperature",
            0.4,
            "top_p",
            0.5,
            "max_tokens",
            450,
            "messages",
            List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", user)));

    try {
      Map res =
          client
              .post()
              .uri("/chat/completions")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
              .bodyValue(body)
              .retrieve()
              .bodyToMono(Map.class)
              .block();

      String content = "";
      if (res != null && res.get("choices") instanceof List<?> choices && !choices.isEmpty()) {
        Object msg = ((Map<?, ?>) choices.getFirst()).get("message");
        if (msg instanceof Map<?, ?> m && m.get("content") instanceof String s) {
          content = s;
        }
      }

      content = limitChars(content, maxChars);
      // content = enforceStyle(content);
      return content;

    } catch (WebClientResponseException wex) {
      log.error(
          "OpenAI error {} {}: {}",
          wex.getStatusCode().value(),
          wex.getStatusText(),
          wex.getResponseBodyAsString());
      String fallback = "답변을 생성하는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주시길 바랍니다.";
      return limitChars(fallback, maxChars);
    } catch (Exception e) {
      log.error("OpenAI call failed", e);
      String fallback = "답변을 생성하는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주시길 바랍니다.";
      return limitChars(fallback, maxChars);
    }
  }

  private String limitChars(String s, int max) {
    if (s == null) return "";
    if (s.length() <= max) return s;
    return s.substring(0, max - 3) + "...";
  }

  //  private String enforceStyle(String s) {
  //    String t = s.trim();
  //    if (!(t.endsWith("습니다.")
  //        || t.endsWith("습니다.”")
  //        || t.endsWith("습니다\"")
  //        || t.endsWith("습니다.\""))) {
  //      t = t.replaceAll("[.!?]?\\s*$", "습니다.");
  //    }
  //    return t;
  //  }
}
