package com.hackathon.junglegym.domain.criminalRecord.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.criminalRecord.entity.CriminalRecord;
import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface CriminalRecordRepository extends JpaRepository<CriminalRecord, Long> {

  List<CriminalRecord> findAllByPolitician_Id(Long PoliticianId);

  CriminalRecord findByPoliticianAndTitle(Politician p, String title);
}
