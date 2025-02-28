package com.pcwk.ehr.video;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "VIDEO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VIDEO_ID", nullable = false, unique = true)
    private Long videoId; // ✅ String → Long 변경

    @Column(name = "VIDEO_TITLE", nullable = false)
    private String title;

    @Column(name = "VIDEO_URL", nullable = false)
    private String url;

    @Column(name = "VIDEO_VIEW_CNT", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "VIDEO_REG_DATE", nullable = false)
    private LocalDate regDate;

    @Column(name = "VIDEO_DESCRIPTION")
    private String description;
}
