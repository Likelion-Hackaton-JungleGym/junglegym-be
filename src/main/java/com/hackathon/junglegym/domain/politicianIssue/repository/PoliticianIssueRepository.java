package com.hackathon.junglegym.domain.politicianIssue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politicianIssue.entity.PoliticianIssue;

public interface PoliticianIssueRepository extends JpaRepository<PoliticianIssue, Long> {

  List<PoliticianIssue> findByPolitician(Politician politician);

  List<PoliticianIssue> findByPoliticianId(Long politicianId);
}
