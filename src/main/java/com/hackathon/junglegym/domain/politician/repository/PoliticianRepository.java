package com.hackathon.junglegym.domain.politician.repository;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticianRepository extends JpaRepository<Politician, Long> {

  void deleteByName(String name);

  List<Politician> findAllByRegion_Name(String regionName);

  Optional<Politician> findByName(String name);
}
