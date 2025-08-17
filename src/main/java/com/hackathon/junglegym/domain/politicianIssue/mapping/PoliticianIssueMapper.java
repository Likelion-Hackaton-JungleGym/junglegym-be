package com.hackathon.junglegym.domain.politicianIssue.mapping;

import java.util.List;
import java.util.stream.Collectors;

import com.hackathon.junglegym.domain.politicianIssue.dto.response.PoliticianIssueResponse;
import com.hackathon.junglegym.domain.politicianIssue.entity.PoliticianIssue;

public class PoliticianIssueMapper {

  public static PoliticianIssueResponse toResponse(PoliticianIssue e) {
    return PoliticianIssueResponse.builder()
        .politicianIssueId(e.getId())
        .title(e.getTitle())
        .link(e.getLink())
        .build();
  }

  public static List<PoliticianIssueResponse> toResponseList(List<PoliticianIssue> list) {
    return list.stream().map(PoliticianIssueMapper::toResponse).collect(Collectors.toList());
  }
}
