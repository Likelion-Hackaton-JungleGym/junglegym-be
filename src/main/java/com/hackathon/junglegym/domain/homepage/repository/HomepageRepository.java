package com.hackathon.junglegym.domain.homepage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.homepage.entity.Homepage;
import com.hackathon.junglegym.domain.homepage.entity.LinkType;
import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface HomepageRepository extends JpaRepository<Homepage, Long> {

  List<Homepage> findAllByPolitician_Id(Long PoliticianId);

  Homepage findByPoliticianAndLinkType(Politician p, LinkType lt);
}
