package com.hackathon.junglegym.domain.mediaOrientation.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "MediaCreateRequest DTO", description = "미디어 정보 등록을 위한 데이터 전송")
public class MediaOrientationCreateRequest {

  @Schema(description = "언론사명(PK)", example = "조선일보")
  @NotBlank(message = "media는 필수입니다.")
  private String media;

  @Schema(description = "이미지 URL", example = "https://.../image.png")
  @NotBlank(message = "imgUrl은 필수입니다.")
  private String imgUrl;
}
