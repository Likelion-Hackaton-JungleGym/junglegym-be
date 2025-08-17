package com.hackathon.junglegym.domain.homepage.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.homepage.dto.request.HomepageRequest;
import com.hackathon.junglegym.domain.homepage.dto.response.HomepageResponse;
import com.hackathon.junglegym.domain.homepage.entity.Homepage;
import com.hackathon.junglegym.domain.politician.entity.Politician;

@Component
public class HomepageMapper {

  // Entity -> Response
  public HomepageResponse toHomepageResponse(Homepage homepage) {
    return HomepageResponse.builder()
        .id(homepage.getId())
        .link(homepage.getLink())
        .linkType(homepage.getLinkType())
        .build();
  }

  // Request -> Entity
  public Homepage toHomepage(HomepageRequest homepageRequest, Politician politician) {
    return Homepage.builder()
        .politician(politician)
        .link(homepageRequest.getLink())
        .linkType(homepageRequest.getLinkType())
        .build();
  }
}
