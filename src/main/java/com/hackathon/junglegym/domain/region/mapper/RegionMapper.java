package com.hackathon.junglegym.domain.region.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.region.dto.response.RegionResponse;
import com.hackathon.junglegym.domain.region.entity.Region;

@Component
public class RegionMapper {

  public RegionResponse toRegionResponse(Region region) {
    return RegionResponse.builder().id(region.getId()).name(region.getName()).build();
  }
}
