package com.hackathon.junglegym.domain.qeustion.service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    if (c.getLawType() != null) payload.put("law_type", c.getLawType());
    if (c.getPromulgationNo() != null) payload.put("promulgation_no", c.getPromulgationNo());
    payload.put("revision_id", c.getRevisionId());
    if (c.getSourceFileName() != null) payload.put("source_file", c.getSourceFileName());
    if (c.getChapter() != null) payload.put("chapter", c.getChapter());
    if (c.getChapterTitle() != null) payload.put("chapter_title", c.getChapterTitle());
    if (c.getArticle() != null) payload.put("article", c.getArticle());
    if (c.getArticleTitle() != null) payload.put("article_title", c.getArticleTitle());
    if (c.getChunkIndex() != null) payload.put("chunk_index", c.getChunkIndex());
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
}
