package com.hackathon.junglegym.domain.mediaOrientation.dto.response;

import com.hackathon.junglegym.domain.mediaOrientation.entity.MediaOrientation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "MediaOrientationResponse DTO", description = "미디어 목록 응답")
public class MediaOrientationResponse {

  @Schema(description = "언론사명", example = "조선일보")
  private String media;

  @Schema(description = "이미지 URL")
  private String imgUrl;

  public static MediaOrientationResponse from(MediaOrientation e) {
    return MediaOrientationResponse.builder().media(e.getMedia()).imgUrl(e.getImgUrl()).build();
  }
}
