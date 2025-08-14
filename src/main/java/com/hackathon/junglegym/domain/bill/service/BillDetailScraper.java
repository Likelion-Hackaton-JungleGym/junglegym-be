package com.hackathon.junglegym.domain.bill.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class BillDetailScraper {

  private static final int TIMEOUT_MS = 12_000;
  private static final String UA =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
          + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

  // 상세 페이지에서 ‘제안이유 및 주요내용’만 추출
  public String fetchMainContent(String detailUrl) throws Exception {
    Document doc =
        Jsoup.connect(detailUrl)
            .userAgent(UA)
            .referrer("https://likms.assembly.go.kr/") // 가벼운 우회
            .timeout(TIMEOUT_MS)
            .ignoreContentType(true)
            .followRedirects(true)
            .get();

    // 1) 펼친 전체 본문이 있으면 우선 사용
    Element content = doc.selectFirst("#summaryHiddenContentDiv.textType02");
    // 2) 없으면 접힌/요약 본문 사용
    if (content == null) {
      content = doc.selectFirst("#summaryContentDiv.textType02");
    }
    if (content == null) {
      return null;
    }

    // <br> → 줄바꿈, </p> → 빈 줄로 변환한 뒤 텍스트화
    String html = content.html().replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("(?i)</p>", "\n\n");
    String text = Jsoup.parse(html).text(); // 태그 제거(우리가 넣은 \n은 유지됨)

    // 머리말 토막(“제안이유”, “주요내용”)은 깔끔하게 제거
    text = text.replaceFirst("^\\s*제안이유\\s*", "").replaceFirst("\\s*주요내용\\s*", "");

    // 공백 정리
    text =
        text.replace('\u00A0', ' ') // NBSP → space
            .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
            .replaceAll("\\n{3,}", "\n\n") // 과도한 빈 줄 축소
            .trim();

    text = text.replace("감추기", " ").replace("▶", " ").trim();
    return text.isBlank() ? null : text;
  }
}
