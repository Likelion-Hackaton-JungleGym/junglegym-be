package com.hackathon.junglegym.domain.criminalRecord.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.response.CriminalRecordResponse;
import com.hackathon.junglegym.domain.criminalRecord.entity.CriminalRecord;
import com.hackathon.junglegym.domain.politician.entity.Politician;

@Component
public class CriminalRecordMapper {

  // Entity -> Response
  public CriminalRecordResponse toCriminalRecordResponse(CriminalRecord criminalRecord) {
    return CriminalRecordResponse.builder()
        .id(criminalRecord.getId())
        .title(criminalRecord.getTitle())
        .fine(criminalRecord.getFine())
        .build();
  }

  // Request -> Entity
  public CriminalRecord toCriminalRecord(CriminalRecordRequest request, Politician politician) {
    return CriminalRecord.builder()
        .politician(politician)
        .title(request.getTitle())
        .fine(request.getFine())
        .build();
  }
}
