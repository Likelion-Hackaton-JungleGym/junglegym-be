package com.hackathon.junglegym.domain.property.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.property.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Long> {

  Optional<Property> findByPolitician_Id(Long PoliticianId);

  void deleteByPolitician(Politician politician);
}
