package com.pcwk.ehr.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    @NotBlank(message = "비디오 제목은 필수입니다")
    private String title;

    @NotBlank(message = "YouTube URL은 필수입니다")
    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?(youtube\\.com\\/watch\\?v=|youtu\\.be\\/)[a-zA-Z0-9_-]{11}(&.*)?$", 
             message = "유효한 YouTube URL이 아닙니다")
    private String url;

    private String description;

    // 새 비디오 생성 시 자동으로 설정됨
    private LocalDate regDate;
}