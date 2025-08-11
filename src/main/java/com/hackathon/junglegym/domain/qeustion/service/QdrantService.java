package com.hackathon.junglegym.domain.qeustion.service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.hackathon.junglegym.domain.qeustion.dto.Chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Qdrant 컬렉션 생성 및 업서트
@Service
@RequiredArgsConstructor
@Slf4j
public class QdrantService {

  @Value("${qdrant.base-url}")
  private String qdrantUrl;

  @Value("${qdrant.collection}")
  private String collection;

  @Value("${qdrant.vector-size}")
  private int vectorSize;

  private WebClient client;

  @PostConstruct
  public void init() {
    client = WebClient.create(qdrantUrl);
    try {
      ensureCollection();
    } catch (Exception e) {
      log.warn("ensureCollection failed: {}", e.toString());
    }
  }

  public void ensureCollection() {

    try {
      client.get().uri("/collections/{c}", collection).retrieve().toBodilessEntity().block();

      return;
    } catch (WebClientResponseException.NotFound nf) {

    }

    Map<String, Object> cfg = Map.of("vectors", Map.of("size", vectorSize, "distance", "Cosine"));

    try {
      client
          .put()
          .uri("/collections/{c}", collection)
          .bodyValue(cfg)
          .retrieve()
          .toBodilessEntity()
          .block();
    } catch (WebClientResponseException.Conflict e) {
      log.debug("Collection {} already exists (409)", collection);
    }
  }

  public String getCollection() {
    return collection;
  }

  public void upsert(Chunk c, float[] vec) {
    String deterministicUuid =
        UUID.nameUUIDFromBytes(c.getId().getBytes(StandardCharsets.UTF_8)).toString();

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("law_name", c.getLawName());
    if (c.getLawType() != null) {
      payload.put("law_type", c.getLawType());
    }
    if (c.getPromulgationNo() != null) {
      payload.put("promulgation_no", c.getPromulgationNo());
    }
    payload.put("revision_id", c.getRevisionId());
    if (c.getSourceFileName() != null) {
      payload.put("source_file", c.getSourceFileName());
    }
    if (c.getChapter() != null) {
      payload.put("chapter", c.getChapter());
    }
    if (c.getChapterTitle() != null) {
      payload.put("chapter_title", c.getChapterTitle());
    }
    if (c.getArticle() != null) {
      payload.put("article", c.getArticle());
    }
    if (c.getArticleTitle() != null) {
      payload.put("article_title", c.getArticleTitle());
    }
    if (c.getChunkIndex() != null) {
      payload.put("chunk_index", c.getChunkIndex());
    }
    payload.put("text", c.getText());

    Map<String, Object> point = new LinkedHashMap<>();
    point.put("id", deterministicUuid);
    point.put("vector", vec);
    point.put("payload", payload);
    Map<String, Object> body = Map.of("points", List.of(point));

    client
        .put()
        .uri("/collections/{c}/points?wait=true", collection)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public static class Hit {

    public final String lawName;
    public final Integer article;
    public final String articleTitle;
    public final String snippet;
    public final Double score;

    public Hit(String lawName, Integer article, String articleTitle, String snippet, Double score) {
      this.lawName = lawName;
      this.article = article;
      this.articleTitle = articleTitle;
      this.snippet = snippet;
      this.score = score;
    }
  }

  @SuppressWarnings("unchecked")
  public List<String> searchSimilar(float[] queryVector, int limit, boolean includeSnippet) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("vector", queryVector);
    body.put("limit", limit);
    body.put("with_payload", true);
    body.put("with_vectors", false);

    Map<?, ?> res;
    try {
      res =
          client
              .post()
              .uri("/collections/{c}/points/search", collection)
              .bodyValue(body)
              .retrieve()
              .bodyToMono(Map.class)
              .block();
    } catch (WebClientResponseException e) {
      log.error(
          "Qdrant search failed ({}): {}", e.getStatusCode().value(), e.getResponseBodyAsString());
      throw e;
    }

    if (res == null) {
      return List.of();
    }
    Object resultObj = res.get("result");
    if (!(resultObj instanceof List<?> list)) {
      return List.of();
    }

    return list.stream()
        .map(
            item -> {
              Map<String, Object> r = (Map<String, Object>) item;
              Map<String, Object> p = (Map<String, Object>) r.get("payload");
              if (p == null) {
                p = Map.of();
              }

              String lawName = (String) p.getOrDefault("law_name", "");
              Integer article = p.get("article") instanceof Number ar ? ar.intValue() : null;
              String title = (String) p.getOrDefault("article_title", "");
              String text = (String) p.getOrDefault("text", "");

              return formateRelatedLawString(lawName, article, title, text, includeSnippet);
            })
        .collect(Collectors.toList());
  }

  public String searchTop1(float[] queryVector) {
    List<String> list = searchSimilar(queryVector, 5, true);
    return list.isEmpty() ? null : list.getFirst();
  }

  private static String formateRelatedLawString(
      String lawName,
      Integer article,
      String articleTitle,
      String snippet,
      boolean includeSnippet) {

    String titlePart = (articleTitle == null || articleTitle.isBlank()) ? "" : " " + articleTitle;
    String base = "%s 제%d조%s".formatted(safe(lawName), article == null ? 0 : article, titlePart);

    if (!includeSnippet) {
      return base;
    }

    if (snippet == null || snippet.isBlank()) {
      return base;
    }
    String sn = cut(snippet.trim(), 180);
    return base + "「" + sn + "」";
  }

  private static String cut(String s, int max) {
    if (s == null) {
      return null;
    }
    if (s.length() <= max) {
      return s;
    }
    return s.substring(0, max - 3) + "...";
  }

  private static String safe(String s) {
    return s == null ? "" : s;
  }
}
