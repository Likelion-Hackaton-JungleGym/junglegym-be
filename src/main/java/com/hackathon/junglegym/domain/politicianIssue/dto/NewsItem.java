package com.hackathon.junglegym.domain.politicianIssue.dto;

import java.time.ZonedDateTime;

public record NewsItem(
    String title,
    String link,
    String source,
    ZonedDateTime publishedAt,
    String snippet // 메타/상세에서 뽑은 1~2문단 요약 텍스트(판별/요약에 사용)
    ) {}
