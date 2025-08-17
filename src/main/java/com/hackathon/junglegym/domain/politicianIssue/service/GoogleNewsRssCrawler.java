package com.hackathon.junglegym.domain.politicianIssue.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hackathon.junglegym.domain.politicianIssue.dto.NewsItem;

/** Google 뉴스 RSS 크롤러 (원문 접속/리다이렉트 추적/본문 파싱 없음 → 빠름) */
public class GoogleNewsRssCrawler {

  private final HttpClient http = HttpClient.newHttpClient();

  /** query 그대로 사용하여 검색(UTF-8 인코딩), 최신 항목부터 최대 limit개 수집 */
  public List<NewsItem> fetch(String query, int limit) throws Exception {
    String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
    String url = "https://news.google.com/rss/search?hl=ko&gl=KR&ceid=KR%3Ako&q=" + q;

    HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
    HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
    if (resp.statusCode() >= 400) {
      throw new RuntimeException("RSS fetch failed " + resp.statusCode());
    }

    Document doc = Jsoup.parse(resp.body(), "", org.jsoup.parser.Parser.xmlParser());
    List<NewsItem> out = new ArrayList<>();
    for (Element item : doc.select("item")) {
      String title = text(item, "title");
      String link = text(item, "link"); // 구글 redirect 링크(그대로 사용)
      String source = text(item, "source"); // 언론사명
      String pub = text(item, "pubDate");
      ZonedDateTime publishedAt = parseRfc822(pub);

      out.add(new NewsItem(title, link, source, publishedAt, null));
      if (out.size() >= Math.max(10, limit)) {
        break;
      }
    }
    return out;
  }

  private String text(Element item, String tag) {
    Element el = item.selectFirst(tag);
    return el == null ? null : el.text();
  }

  private ZonedDateTime parseRfc822(String pub) {
    try {
      return ZonedDateTime.parse(pub, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME)
          .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    } catch (Exception e) {
      return null;
    }
  }
}
