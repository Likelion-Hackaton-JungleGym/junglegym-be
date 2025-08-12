package com.hackathon.junglegym.domain.politician.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PoliticianInfoOpenApi {

  @Value("${openapi.service-key}")
  private String serviceKey;

  @Value("${openapi.info.base-url}")
  private String baseUrl;

  private final int pSize = 50;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public List<JsonNode> fetchAllPages(String arrayKey) throws Exception {
    List<JsonNode> all = new ArrayList<>();
    int page = 1;

    while (true) {
      HttpURLConnection conn = null;

      try {
        // 1. URL 설정
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        // 2. url 요청 규격 설정
        urlBuilder
            .append("?")
            .append(URLEncoder.encode("KEY", "UTF-8"))
            .append("=")
            .append(URLEncoder.encode(serviceKey, "UTF-8"));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("type", "UTF-8"))
            .append("=")
            .append(URLEncoder.encode("json", "UTF-8"));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("pIndex", "UTF-8"))
            .append("=")
            .append(URLEncoder.encode(String.valueOf(page), "UTF-8"));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("pSize", "UTF-8"))
            .append("=")
            .append(URLEncoder.encode(String.valueOf(pSize), "UTF-8"));

        // 3. url 객체 생성
        URL url = (new URI(urlBuilder.toString())).toURL();
        // 4. Connection 객체 생성
        conn = (HttpURLConnection) url.openConnection();
        // 5. 통신을 위한 메소드 set
        conn.setRequestMethod("GET");
        // 6. 통신을 위한 Content-type set
        conn.setRequestProperty("Content-type", "application/json");
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
          try (InputStream is = conn.getErrorStream()) {
            if (is != null) {
              String err = new String(is.readAllBytes());
              System.out.println("[OPENAPI] HTTP " + code + " : " + err);
            } else {
              System.out.println("[OPENAPI] HTTP " + code + " (no body)");
            }
          }
        }

        JsonNode root;
        try (InputStream is = conn.getInputStream()) {
          root = objectMapper.readTree(is);
        }

        JsonNode arr = root.findPath(arrayKey);

        if (arr.isMissingNode()) {
          break;
        }

        int got = 0;
        if (arr.isArray()) {
          for (JsonNode n : arr) {
            all.add(n);
            got++;
          }
        } else {
          all.add(arr);
          got = 1;
        }

        if (got < pSize) {
          break;
        }
        page++;
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }

    return all;
  }

  public String text(JsonNode n, String field) {
    JsonNode f = n.get(field);
    return (f == null || f.isNull() || f.asText().isBlank()) ? null : f.asText().trim();
  }
}
