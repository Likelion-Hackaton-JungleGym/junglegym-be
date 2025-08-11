package com.hackathon.junglegym.domain.qeustion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.qeustion.dto.ArticleClause;
import com.hackathon.junglegym.domain.qeustion.dto.Chunk;

// 조문 -> 청크 분할 유틸
@Component
public class Chunker {

  private static final int MAX_LEN = 700;
  private static final int OVERLAP = 100;

  public List<Chunk> chunk(ArticleClause a) {
    List<Chunk> out = new ArrayList<>();
    String t = normalize(a.getText());
    int idx = 0, chunkIdx = 0;

    while (idx < t.length()) {
      int end = Math.min(t.length(), idx + MAX_LEN);
      int adj = adjustAtSentenceEnd(t, idx, end);
      end = Math.max(Math.min(adj, t.length()), idx + 1);

      String piece = t.substring(idx, end).trim();
      if (!piece.isEmpty()) {
        String id =
            "%s-%s-%s-%d"
                .formatted(
                    a.getLawName(),
                    a.getChapter() == null ? "0" : a.getChapter(),
                    a.getArticle() == null ? "0" : a.getArticle(),
                    chunkIdx);

        out.add(
            Chunk.builder()
                .id(id)
                .lawName(a.getLawName())
                .lawType(a.getLawType())
                .promulgationNo(a.getPromulgationNo())
                .revisionId(a.getRevisionId())
                .sourceFileName(a.getSourceFileName())
                .chapter(a.getChapter())
                .chapterTitle(a.getChapterTitle())
                .article(a.getArticle())
                .articleTitle(a.getArticleTitle())
                .chunkIndex(chunkIdx)
                .text(piece)
                .tokens(0)
                .build());
      }
      if (end >= t.length()) break;
      idx = Math.max(end - OVERLAP, idx + 1);
      chunkIdx++;
    }
    return out;
  }

  private String normalize(String s) {
    return s.replaceAll("\\s+", " ").trim();
  }

  private int adjustAtSentenceEnd(String t, int start, int end) {
    int e = end;
    while (e > start
        && e < t.length()
        && t.charAt(e - 1) != '.'
        && t.charAt(e - 1) != '다'
        && t.charAt(e - 1) != '!'
        && t.charAt(e - 1) != '?') {
      e--;
      if (end - e > 80) break;
    }
    return e;
  }
}
