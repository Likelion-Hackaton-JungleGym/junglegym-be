package com.hackathon.junglegym.domain.qeustion.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

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
    String instructions =
        String.join(
            "\n",
            "당신은 대한민국 법령 기반 정치 해설 어시스턴트입니다.",
            "반드시 '-습니다' 체로만 답변합니다.",
            "법령 컨텍스트는 '근거'로 정확히 인용합니다.",
            "질문이 인물/기관/현황 등과 같은 최신 '사실'이 필요한 경우 web_search 도구를 사용해 보완하고, 기준일을 명시합니다.",
            "모르면 '죄송합니다. 해당 질문은 답변드릴 수 없습니다.'라고 답합니다.",
            "답변은 최대 700자 이내로 합니다.",
            "정치적으로 편향된 해석을 하지 않습니다. 무조건 중립적으로 답변합니다.",
            "출력 형식: 1)요지(필요시 인물/날짜 등 사실 포함). 2)근거 (법령명 제x조)");

    try {
      JsonNode res =
          client
              .post()
              .uri("/responses")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
              .bodyValue(getStringObjectMap(question, context, instructions))
              .retrieve()
              .bodyToMono(JsonNode.class)
              .block();

      return limitChars(extractText(res), maxChars);

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

  private static Map<String, Object> getStringObjectMap(
      String question, String context, String instructions) {
    String today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).toString();
    String user =
        "컨텍스트:\n"
            + context
            + "\n\n질문:\n"
            + question
            + "\n\n기준일: "
            + today
            + "\n\n지시: 최신 인물/기관/현황이 필요하면 web_search 도구를 사용해 한줄로 보완하고, 반드시 근거 법조문을 함께 제시하시오.\n"
            + today
            + "자료를 우선하시오.";

    Map<String, Object> body =
        Map.of(
            "model",
            "gpt-4o-mini",
            "tools",
            List.of(Map.of("type", "web_search_preview")),
            "tool_choice",
            "required",
            "temperature",
            0.2,
            "top_p",
            0.5,
            "max_output_tokens",
            450,
            "instructions",
            instructions,
            "input",
            List.of(Map.of("role", "user", "content", user)));
    return body;
  }

  private String limitChars(String s, int max) {
    if (s == null) return "";
    if (s.length() <= max) return s;
    return s.substring(0, max - 3) + "...";
  }

  private String extractText(JsonNode res) {
    if (res == null) return "";

    String outputText = res.path("output_text").asText("");
    if (!outputText.isBlank()) return outputText;

    return res.path("output").findValues("text").stream()
        .map(n -> n.isTextual() ? n.asText() : n.path("value").asText(""))
        .collect(Collectors.joining())
        .trim();
  }
}
