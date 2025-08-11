package com.hackathon.junglegym.domain.qeustion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.hackathon.junglegym.domain.qeustion.dto.Embeddings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// OpenAI 임베딩 호출
@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

  private final WebClient webClient =
      WebClient.builder()
          .baseUrl("https://api.openai.com/v1")
          .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
          .build();

  @Value("${openai.api-key}")
  private String apiKey;

  public float[] embed(String text) {
    try {
      var res =
          webClient
              .post()
              .uri("/embeddings")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
              .bodyValue(new EmbeddingRequest("text-embedding-3-small", text))
              .retrieve()
              .bodyToMono(Embeddings.class)
              .block();

      if (res == null || res.getData() == null || res.getData().isEmpty()) {
        throw new IllegalStateException("Empty embeddings response");
      }

      List<Double> vec = res.getData().getFirst().getEmbedding();
      float[] arr = new float[vec.size()];
      for (int i = 0; i < vec.size(); i++) {
        arr[i] = vec.get(i).floatValue();
      }
      return arr;
    } catch (WebClientResponseException wex) {
      log.error(
          "OpenAI error {} {}: {}",
          wex.getStatusCode().value(),
          wex.getStatusText(),
          wex.getResponseBodyAsString());
      throw new RuntimeException("OpenAI embedding call failed" + wex.getStatusCode().value(), wex);
    } catch (Exception e) {
      throw new RuntimeException("OpenAI embedding call failed", e);
    }
  }

  private record EmbeddingRequest(String model, Object input) {}
}
