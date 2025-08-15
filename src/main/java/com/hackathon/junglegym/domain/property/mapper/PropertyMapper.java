package com.hackathon.junglegym.domain.property.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.property.dto.request.PropertyRequest;
import com.hackathon.junglegym.domain.property.dto.response.PropertyResponse;
import com.hackathon.junglegym.domain.property.entity.Property;

@Component
public class PropertyMapper {

  // Entity -> Response
  public PropertyResponse toPropertyResponse(Property property) {
    return PropertyResponse.builder()
        .id(property.getId())
        .totalCapital(property.getTotalCapital())
        .totalDebt(property.getTotalDebt())
        .capital(property.getCapital())
        .build();
  }

  // Request -> Entity
  public Property toProperty(PropertyRequest request, Politician politician) {
    return Property.builder()
        .politician(politician)
        .totalCapital(request.getTotalCapital())
        .totalDebt(request.getTotalDebt())
        .capital(request.getCapital())
        .build();
  }
}
