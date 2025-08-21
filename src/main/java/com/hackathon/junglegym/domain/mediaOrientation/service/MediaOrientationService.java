package com.hackathon.junglegym.domain.mediaOrientation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.mediaOrientation.dto.request.MediaOrientationCreateRequest;
import com.hackathon.junglegym.domain.mediaOrientation.dto.response.MediaOrientationResponse;
import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;
import com.hackathon.junglegym.domain.mediaOrientation.exception.MediaOrientationErrorCode;
import com.hackathon.junglegym.domain.mediaOrientation.repository.MediaOrientationRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaOrientationService {

  private final MediaOrientationRepository repository;

  @Transactional
  public MediaOrientationResponse create(MediaOrientationCreateRequest req) {
    if (repository.existsById(req.getMedia())) {
      throw new CustomException(MediaOrientationErrorCode.MEDIA_EXISTS);
    }
    MediaOrientation saved =
        repository.save(
            MediaOrientation.builder().media(req.getMedia()).imgUrl(req.getImgUrl()).build());
    return MediaOrientationResponse.from(saved);
  }

  @Transactional
  public List<MediaOrientationResponse> list() {
    return repository.findAll().stream().map(MediaOrientationResponse::from).toList();
  }

  @Transactional
  public void delete(String media) {
    if (!repository.existsById(media)) {
      throw new CustomException(MediaOrientationErrorCode.MEDIA_NOT_FOUND);
    }
    repository.deleteById(media);
  }
}
