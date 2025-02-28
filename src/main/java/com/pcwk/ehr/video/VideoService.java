package com.pcwk.ehr.video;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }
    @Transactional
    public Video getVideoAndIncrementViewCount(Long id) {
        try {
            Video video = videoRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("해당 비디오를 찾을 수 없습니다. ID: " + id));

            log.info("✅ 비디오 불러오기 성공: {}", video.getTitle());

            video.setViewCount((video.getViewCount() != null ? video.getViewCount() : 0) + 1);
            return videoRepository.save(video);
        } catch (Exception e) {
            log.error("❌ 비디오 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    public String extractYoutubeId(String url) {
        if (url == null || url.isEmpty()) {
            return "dQw4w9WgXcQ"; // 기본 썸네일 ID
        }

        String videoId = "";
        if (url.contains("youtube.com/watch?v=")) {
            videoId = url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            videoId = url.split("youtu.be/")[1].split("\\?")[0];
        }

        return videoId.isEmpty() ? "dQw4w9WgXcQ" : videoId;
    }
    public Page<Video> getPagedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("videoId")));
        return videoRepository.findAll(pageable);
    }

 
}
