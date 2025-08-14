package com.hackathon.junglegym.domain.bill.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.junglegym.domain.politician.exception.OpenApiErrorCode;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillOpenApi {

  @Value("${openapi.service-key}")
  private String serviceKey;

  @Value("${openapi.bill.base-url}")
  private String baseUrl;

  @Value("${openapi.bill.age}") // 필수: 대수(21/22 등)
  private String age;

  private final int pSize = 100; // 문서 기본값 100
  private final ObjectMapper om = new ObjectMapper();

  /** 대표발의자/제안자 이름 기준 전체 페이지 수집 (arrayKey는 대개 "row") */
  public List<JsonNode> fetchAllByProposer(String proposerName, String arrayKey) throws Exception {
    List<JsonNode> all = new ArrayList<>();
    int page = 1;

    while (true) {
      HttpURLConnection conn = null;
      try {
        StringBuilder urlBuilder =
            new StringBuilder(baseUrl)
                .append("?KEY=")
                .append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8))
                .append("&type=json")
                .append("&pIndex=")
                .append(page)
                .append("&pSize=")
                .append(pSize)
                .append("&AGE=")
                .append(URLEncoder.encode(age, StandardCharsets.UTF_8)) // 필수
                // 문서 기준: PROPOSER(제안자 검색어), 혹은 RST_PROPOSER(대표발의자) 파라미터가 제공되면 그걸 사용
                .append("&PROPOSER=")
                .append(URLEncoder.encode(proposerName, StandardCharsets.UTF_8));

        URL url = (new URI(urlBuilder.toString())).toURL();
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        int code = conn.getResponseCode();
        InputStream body = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();
        if (body == null || code >= 400) {
          throw new CustomException(OpenApiErrorCode.OPENAPI_HTTP_ERROR);
        }

        JsonNode root;
        try (body) {
          root = om.readTree(body);
        } catch (Exception e) {
          throw new CustomException(OpenApiErrorCode.OPENAPI_PARSE_ERROR);
        }

        JsonNode arr = root.findPath(arrayKey);
        if (arr.isMissingNode() || arr.isNull()) {
          throw new CustomException(OpenApiErrorCode.OPENAPI_ARRAY_KEY_MISSING);
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
          break; // 마지막 페이지
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
