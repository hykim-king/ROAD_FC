package com.pcwk.ehr.video;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "비디오 URL은 필수입니다")
    private String url;

    private String description;

    // 새 비디오 생성 시 자동으로 설정됨
    private LocalDate regDate;
}