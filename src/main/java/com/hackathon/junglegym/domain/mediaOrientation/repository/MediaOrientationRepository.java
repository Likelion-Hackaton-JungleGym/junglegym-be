package com.hackathon.junglegym.domain.mediaOrientation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;

public interface MediaOrientationRepository extends JpaRepository<MediaOrientation, String> {}
