package com.hackathon.junglegym.domain.qeustion.service;

// 전체 Ingest 흐름 제어

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hackathon.junglegym.domain.qeustion.dto.ArticleClause;
import com.hackathon.junglegym.domain.qeustion.dto.Chunk;
import com.hackathon.junglegym.domain.qeustion.dto.IngestResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestService {

  private final FilenameParser filenameParser;
  private final PdfParser pdfParser;
  private final Chunker chunker;
  private final EmbeddingService embeddingService;
  private final QdrantService qdrantService;

  public IngestResult ingestPdfAuto(MultipartFile file) throws IOException {

    final long start = System.currentTimeMillis();
    final String sourceFile = file.getOriginalFilename();

    int totalArticles = 0, totalChunks = 0, success = 0, failed = 0;
    List<String> warnings = new ArrayList<>();

    // 1. 파일명에서 메타 자동 추출
    FilenameParser.Parsed p = null;
    try {
      p = filenameParser.parse(sourceFile);
    } catch (Exception e) {
      log.warn("Filename parse failed: {}", sourceFile, e);
      warnings.add("filename-parse-failed");
    }

    String lawName = p != null ? p.lawName() : "법령";
    String lawType = p != null ? p.lawType() : null;
    String promulgationNo = p != null ? p.promulgationNo() : null;
    String revisionId = p != null ? p.revisionId() : "current";

    // 2. Qdrant 컬렉션 준비
    try {
      qdrantService.ensureCollection();
    } catch (Exception e) {
      log.warn("Qdrant ensureCollection failed", e);
      warnings.add("qdrant-ensure-collection-failed");
    }

    // 3. pdf -> ArticleClause (dto)
    List<ArticleClause> articles;
    try (InputStream in = file.getInputStream()) {
      articles = pdfParser.parse(in, lawName, lawType, promulgationNo, revisionId, sourceFile);
    } catch (Exception e) {
      log.error("PDF parse error: {}", sourceFile, e);
      return IngestResult.builder()
          .lawName(lawName)
          .lawType(lawType)
          .promulgationNo(promulgationNo)
          .revisionId(revisionId)
          .collection(qdrantService.getCollection())
          .sourceFileName(sourceFile)
          .totalArticles(0)
          .totalChunks(0)
          .success(0)
          .failed(0)
          .elapsedMs(System.currentTimeMillis() - start)
          .warnings(String.join(", ", warnings.isEmpty() ? List.of("pdf-parse-error") : warnings))
          .build();
    }
    totalArticles = articles.size();

    // 4. chunking
    List<Chunk> chunks = new ArrayList<>();
    for (ArticleClause a : articles) {
      try {
        chunks.addAll(chunker.chunk(a));
      } catch (Exception e) {
        log.warn("Chunking failed (article {}): {}", a.getArticle(), e.toString());
        warnings.add("chunk-fialed-article-" + a.getArticle());
      }
    }
    totalChunks = chunks.size();

    // 5. Embedding + 업서트
    for (Chunk c : chunks) {
      try {
        float[] vec = embeddingService.embed(c.getText());
        qdrantService.upsert(c, vec);
        success++;
      } catch (Exception e) {
        failed++;
        warnings.add("upsert-failed:" + c.getId());
        log.warn("Embedding/Upsert failed for {}: {}", c.getId(), e.toString());
      }
    }

    return IngestResult.builder()
        .lawName(lawName)
        .lawType(lawType)
        .promulgationNo(promulgationNo)
        .revisionId(revisionId)
        .collection(qdrantService.getCollection())
        .sourceFileName(sourceFile)
        .totalArticles(totalArticles)
        .totalChunks(totalChunks)
        .success(success)
        .failed(failed)
        .elapsedMs(System.currentTimeMillis() - start)
        .warnings(warnings.isEmpty() ? null : String.join(", ", warnings))
        .build();
  }
}
