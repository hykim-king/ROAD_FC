package com.pcwk.ehr.video;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    // 새 비디오 생성 메서드
    @Transactional
    public Video createVideo(VideoDTO videoDTO) {
        try {
            Video video = new Video();
            video.setTitle(videoDTO.getTitle());
            video.setUrl(videoDTO.getUrl());
            video.setDescription(videoDTO.getDescription());
            video.setRegDate(LocalDate.now());
            video.setViewCount(0);
            Video savedVideo = videoRepository.save(video);
            log.info("✅ Video created successfully: {}", savedVideo.getTitle());
            return savedVideo;
        } catch (Exception e) {
            log.error("❌ Error creating video: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create video.", e);
        }
    }

    //영상 삭제
    public void delete(Video videoId) {
        log.info("Deleting video: {}", videoId);
        videoRepository.delete(videoId);
    }
    
    //조회수 증가 로직
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
    //썸네일표시를 위해 유투브 영상 id 분리
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
    //페이징
    public Page<Video> getPagedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("videoId")));
        return videoRepository.findAll(pageable);
        
    }
    //이전영상띄우기
    public List<Video> getPreviousVideos(Long videoId) {
        try {
            List<Video> prevVideos = videoRepository.findTop4ByVideoIdLessThanOrderByVideoIdDesc(videoId);
            log.info("✅ 이전 영상 {}개 불러오기 성공", prevVideos.size());
            return prevVideos;
        } catch (Exception e) {
            log.error("❌ 이전 영상 조회 중 예외 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
  

 // ID로 비디오 조회 메서드
    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
            .orElseThrow(() -> new DataNotFoundException("해당 비디오를 찾을 수 없습니다. ID: " + videoId));
    }
}

