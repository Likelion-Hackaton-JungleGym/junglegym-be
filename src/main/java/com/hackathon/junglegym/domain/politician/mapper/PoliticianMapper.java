package com.hackathon.junglegym.domain.politician.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.politician.dto.request.PoliticianRequest;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianByRegionResponse;
import com.hackathon.junglegym.domain.politician.dto.response.PoliticianResponse;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.region.entity.Region;

@Component
public class PoliticianMapper {

  // Entity -> Response
  public PoliticianResponse toPoliticianResponse(Politician politician) {
    return PoliticianResponse.builder()
        .id(politician.getId())
        .regionName(politician.getRegion().getName())
        .name(politician.getName())
        .polyName(politician.getPolyName())
        .role(politician.getRole())
        .committee(politician.getCommittee())
        .birth(politician.getBirth())
        .retryNumber(politician.getRetryNumber())
        .retryUnit(politician.getRetryUnit())
        .profileImg(politician.getProfileImg())
        .careerSummary(politician.getCareerSummary())
        .military(politician.getMilitary())
        .roleName(politician.getRoleName())
        .regionText(politician.getRegionText())
        .build();
  }

  // Response + RegionName -> Entity
  public Politician toPolitician(PoliticianRequest request, Region region) {
    return Politician.builder()
        .region(region)
        .name(request.getName())
        .polyName(request.getPolyName())
        .role(request.getRole())
        .committee(request.getCommittee())
        .birth(request.getBirth())
        .retryNumber(request.getRetryNumber())
        .retryUnit(request.getRetryUnit())
        .careerSummary(request.getCareerSummary())
        .military(request.getMilitary())
        .roleName(request.getRoleName())
        .regionText(request.getRegionText())
        .build();
  }

  public PoliticianByRegionResponse toPoliticianByRegionResponse(Politician politician) {
    return PoliticianByRegionResponse.builder()
        .id(politician.getId())
        .regionName(politician.getRegion().getName())
        .name(politician.getName())
        .polyName(politician.getPolyName())
        .role(politician.getRole())
        .profileImg(politician.getProfileImg())
        .roleName(politician.getRoleName())
        .build();
  }
}
