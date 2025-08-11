package com.hackathon.junglegym.domain.qeustion.service;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.qeustion.dto.ArticleClause;

// PDF -> 조문/항 단위 텍스트
@Component
public class PdfParser {

  private static final Pattern CHAPTER =
      Pattern.compile("^제\\s*(\\d+)\\s*장\\s*([^\\n]*)$", Pattern.MULTILINE);
  private static final Pattern ARTICLE =
      Pattern.compile("^제\\s*(\\d+)\\s*조\\s*(?:\\(([^)]+)\\))?", Pattern.MULTILINE);

  public List<ArticleClause> parse(
      InputStream pdf,
      String lawName,
      String lawType,
      String promulgationNo,
      String revisionId,
      String sourceFileName)
      throws Exception {
    String raw;

    try (PDDocument doc = PDDocument.load(pdf)) {
      PDFTextStripper stripper = new PDFTextStripper();
      raw = stripper.getText(doc);
    }

    String cleaned = cleanup(raw);

    // 장 수집
    Map<Integer, String> chapTitleByIdx = new HashMap<>();
    List<Span> chapterSpans = new ArrayList<>();
    Matcher ch = CHAPTER.matcher(cleaned);
    while (ch.find()) {
      int idx = ch.start();
      Integer chapNo = Integer.parseInt(ch.group(1));
      String chapTitle = ch.group(2) != null ? ch.group(2).trim() : null;
      chapTitleByIdx.put(idx, chapTitle);
      chapterSpans.add(new Span(idx, -1, chapNo, chapTitle));
    }
    chapterSpans.sort(Comparator.comparingInt(s -> s.start));
    for (int i = 0; i < chapterSpans.size() - 1; i++) {
      chapterSpans.get(i).end = chapterSpans.get(i + 1).start;
    }
    if (!chapterSpans.isEmpty()) chapterSpans.getLast().end = cleaned.length();

    List<ArticleClause> out = new ArrayList<>();
    if (chapterSpans.isEmpty()) {
      out.addAll(
          parseArticlesInBlock(
              cleaned, null, null, lawName, lawType, promulgationNo, revisionId, sourceFileName));
    } else {
      for (Span chap : chapterSpans) {
        String chapBlock = cleaned.substring(chap.start, chap.end);
        out.addAll(
            parseArticlesInBlock(
                chapBlock,
                chap.no,
                chap.title,
                lawName,
                lawType,
                promulgationNo,
                revisionId,
                sourceFileName));
      }
    }
    return out;
  }

  private List<ArticleClause> parseArticlesInBlock(
      String block,
      Integer chapNo,
      String chapTitle,
      String lawName,
      String lawType,
      String promulgationNo,
      String revisionId,
      String sourceFileName) {
    List<ArticleClause> list = new ArrayList<>();

    // 조문 위치, 제목 수집
    List<Span> articleSpans = new ArrayList<>();
    Matcher am = ARTICLE.matcher(block);
    while (am.find()) {
      articleSpans.add(new Span(am.start(), am.end(), Integer.parseInt(am.group(1)), am.group(2)));
    }
    if (articleSpans.isEmpty()) return list;

    // 조문 본문 추출
    for (int i = 0; i < articleSpans.size(); i++) {
      Span a = articleSpans.get(i);
      int contentStart = a.end;
      int contentEnd =
          (i < articleSpans.size() - 1) ? articleSpans.get(i + 1).start : block.length();
      String body = normalize(block.substring(contentStart, contentEnd));
      if (body.isEmpty()) continue;

      list.add(
          ArticleClause.builder()
              .lawName(lawName)
              .lawType(lawType)
              .promulgationNo(promulgationNo)
              .revisionId(revisionId)
              .sourceFileName(sourceFileName)
              .pageStart(null)
              .pageEnd(null)
              .chapter(chapNo)
              .chapterTitle(chapTitle)
              .article(a.no)
              .articleTitle(a.title)
              .text(body)
              .build());
    }
    return list;
  }

  private String cleanup(String raw) {
    StringBuilder sb = new StringBuilder();
    for (String line : raw.split("\\R")) {
      String t = line.replace('\u00A0', ' ').trim();
      if (t.isEmpty()) continue;
      if (t.matches("^\\d+$")) continue;
      if (t.contains("법제처") && t.contains("국가법령정보센터")) continue;
      sb.append(t).append("\n");
    }
    return sb.toString();
  }

  private String normalize(String s) {
    return s.replaceAll("\\s+", " ").trim();
  }

  private static class Span {
    int start, end;
    Integer no;
    String title;

    Span(int start, int end, Integer no, String title) {
      this.start = start;
      this.end = end;
      this.no = no;
      this.title = title;
    }

    Span(int start, int end, Integer no) {
      this(start, end, no, null);
    }
  }
}
