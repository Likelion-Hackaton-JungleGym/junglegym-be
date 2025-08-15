package com.hackathon.junglegym.domain.criminalRecord.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordDeleteRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.request.CriminalRecordUpdateRequest;
import com.hackathon.junglegym.domain.criminalRecord.dto.response.CriminalRecordResponse;
import com.hackathon.junglegym.domain.criminalRecord.entity.CriminalRecord;
import com.hackathon.junglegym.domain.criminalRecord.exception.CriminalRecordErrorCode;
import com.hackathon.junglegym.domain.criminalRecord.mapper.CriminalRecordMapper;
import com.hackathon.junglegym.domain.criminalRecord.repository.CriminalRecordRepository;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.domain.region.entity.Region;
import com.hackathon.junglegym.domain.region.exception.RegionErrorCode;
import com.hackathon.junglegym.domain.region.repository.RegionRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CriminalRecordService {

  private final CriminalRecordRepository criminalRecordRepository;
  private final CriminalRecordMapper criminalRecordMapper;
  private final PoliticianRepository politicianRepository;
  private final RegionRepository regionRepository;

  // 생성
  @Transactional
  public CriminalRecordResponse createCriminalRecord(CriminalRecordRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (criminalRecordRepository.findByPoliticianAndTitle(politician, request.getTitle()) != null) {
      throw new CustomException(CriminalRecordErrorCode.CRIMINAL_RECORD_ALREADY_EXISTS);
    }

    CriminalRecord criminalRecord = criminalRecordMapper.toCriminalRecord(request, politician);

    return criminalRecordMapper.toCriminalRecordResponse(
        criminalRecordRepository.save(criminalRecord));
  }

  // 전체 조회
  public List<CriminalRecordResponse> getAllCriminalRecord(Long politicianId) {
    Politician politician =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    List<CriminalRecord> criminalRecords =
        criminalRecordRepository.findAllByPolitician_Id(politicianId);
    return criminalRecords.stream().map(criminalRecordMapper::toCriminalRecordResponse).toList();
  }

  // 수정
  @Transactional
  public CriminalRecordResponse updateCriminalRecord(CriminalRecordUpdateRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (criminalRecordRepository.findByPoliticianAndTitle(politician, request.getTitle()) == null) {
      throw new CustomException(CriminalRecordErrorCode.CRIMINAL_RECORD_NOT_FOUND);
    }

    CriminalRecord criminalRecord =
        criminalRecordRepository.findByPoliticianAndTitle(politician, request.getTitle());

    criminalRecord.update(request);

    return (criminalRecordMapper.toCriminalRecordResponse(criminalRecord));
  }

  // 삭제
  @Transactional
  public void deleteCriminalRecord(CriminalRecordDeleteRequest request) {
    Region region =
        regionRepository
            .findByName(request.getRegionName())
            .orElseThrow(() -> new CustomException(RegionErrorCode.REGION_NOT_FOUND));

    Politician politician =
        politicianRepository
            .findByNameAndRegion(request.getName(), region)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (criminalRecordRepository.findByPoliticianAndTitle(politician, request.getTitle()) == null) {
      throw new CustomException(CriminalRecordErrorCode.CRIMINAL_RECORD_NOT_FOUND);
    }

    CriminalRecord criminalRecord =
        criminalRecordRepository.findByPoliticianAndTitle(politician, request.getTitle());

    criminalRecordRepository.delete(criminalRecord);
  }
}
