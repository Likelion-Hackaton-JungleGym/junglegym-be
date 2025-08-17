package com.hackathon.junglegym.domain.politician.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.region.entity.Region;

public interface PoliticianRepository extends JpaRepository<Politician, Long> {

  void deleteByName(String name);

  List<Politician> findAllByRegion_NameIn(Collection<String> regionNames);

  Optional<Politician> findByNameAndRegion(String name, Region region);

  boolean existsById(Long id);
}
