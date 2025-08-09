package com.hackathon.junglegym.domain.property.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.property.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Long> {}
