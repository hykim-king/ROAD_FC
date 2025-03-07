package com.pcwk.ehr.video;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RequestMapping("/video")
@Controller
public class VideoController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private VideoService videoService;

    public VideoController() {
    	log.info("┌──────────────────┐");
		log.info("│VideoController() │");
		log.info("└──────────────────┘");
    }
    
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        log.info("📌 Fetching video list - Page: {}", page);
    	log.info("┌──────────────────┐");
		log.info("│VideoList	 ()  │");
		log.info("└──────────────────┘");
        Page<Video> paging = videoService.getPagedVideos(page, 10);

        List<Map<String, Object>> videoInfoList = new ArrayList<>();
        for (Video video : paging.getContent()) {
            Map<String, Object> videoInfo = new HashMap<>();
            videoInfo.put("video", video);
            videoInfo.put("thumbnailUrl", "https://img.youtube.com/vi/" + videoService.extractYoutubeId(video.getUrl()) + "/mqdefault.jpg");
            videoInfoList.add(videoInfo);
        }

        model.addAttribute("videoInfoList", videoInfoList);
        model.addAttribute("paging", paging);
        log.info(" Total videos: {}, Pages: {}", paging.getTotalElements(), paging.getTotalPages());

        return "video/video_list";
    }

    @GetMapping("/detail/{videoId}")
    public String detail(Model model, @PathVariable("videoId") Long videoId,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        log.info("┌──────────────────┐");
        log.info("│detail() │");
        log.info("└──────────────────┘");
        log.info("Fetching video details - Video ID: {}", videoId);

        try {
            // HttpServletRequest와 HttpServletResponse 파라미터 추가
            Video video = videoService.getVideoAndIncrementViewCount(videoId, request, response);
            if (video == null) {
                log.warn("🚨 Video not found - ID: {}", videoId);
                return "redirect:/error/404";
            }

            String embedUrl = convertToEmbedUrl(video.getUrl());
            log.info("🎥 Converted embed URL: {}", embedUrl);

            // 이전 영상 4개 가져오기
            List<Video> prevVideos = videoService.getPreviousVideos(videoId);

            // 각 비디오에 대한 썸네일 URL 생성
            List<Map<String, Object>> prevVideoInfoList = new ArrayList<>();
            for (Video prevVideo : prevVideos) {
                Map<String, Object> videoInfo = new HashMap<>();
                videoInfo.put("video", prevVideo);
                videoInfo.put("thumbnailUrl", "https://img.youtube.com/vi/" +
                        videoService.extractYoutubeId(prevVideo.getUrl()) + "/mqdefault.jpg");
                prevVideoInfoList.add(videoInfo);
            }

            model.addAttribute("video", video);
            model.addAttribute("embedUrl", embedUrl);
            model.addAttribute("prevVideoInfoList", prevVideoInfoList);

            return "video/video_detail";

        } catch (Exception e) {
            log.error("❌ Error fetching video details: {}", e.getMessage(), e);
            return "redirect:/error/500";
        }
    }
 //YouTube URL을 embed URL로 변환

    private String convertToEmbedUrl(String url) {
        log.info("┌────────────────────────┐");
        log.info("│convertToEmbedUrl()     │");
        log.info("└────────────────────────┘");
        if (url.contains("youtu.be/")) {
            return "https://www.youtube.com/embed/" + url.split("youtu.be/")[1].split("\\?")[0];
        } else if (url.contains("watch?v=")) {
            return "https://www.youtube.com/embed/" + url.split("v=")[1].split("&")[0];
        }
        return url;
    }
    // 비디오 업로드 폼 띄우기
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        log.info("┌──────────────────┐");
        log.info("│ showUploadForm() │");
        log.info("└──────────────────┘");

        model.addAttribute("videoDTO", new VideoDTO());
        return "video/video_upload";
    }

    // 비디오 업로드 처리
    @PostMapping("/upload")
    public String uploadVideo(@Valid @ModelAttribute VideoDTO videoDTO, 
                               BindingResult bindingResult, 
                               Model model) {
        log.info("┌──────────────────┐");
        log.info("│ uploadVideo()    │");
        log.info("└──────────────────┘");

        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            log.warn("유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "video/video_upload";
        }

        try {
            // YouTube ID 추출
            String youtubeId = videoService.extractYoutubeId(videoDTO.getUrl());
            
            // 비디오 생성
            Video createdVideo = videoService.createVideo(videoDTO);
            
            log.info("비디오 업로드 성공: {}", createdVideo.getTitle());
            
            // 업로드 후 목록 페이지로 리다이렉트
            return "redirect:/video/list";
        } catch (Exception e) {
            log.error("비디오 업로드 중 오류 발생: {}", e.getMessage(), e);
            
            // 오류 메시지 추가
            model.addAttribute("errorMessage", "비디오 업로드에 실패했습니다: " + e.getMessage());
            return "video/video_upload";
        }
    }
    //비디오 삭제하기
    @GetMapping("/delete/{videoId}")
    public ResponseEntity<Map<String, String>> deleteVideo(@PathVariable("videoId") Long videoId) {
        log.info("📌 Deleting video - ID: {}", videoId);
        log.info("┌──────────────────┐");
        log.info("│ deleteVideo()    │");
        log.info("└──────────────────┘");
        try {
            Video video = videoService.getVideoById(videoId);
            videoService.delete(video);
            log.info("✅ Video deleted successfully: {}", videoId);
            
            // 성공 응답 메시지 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "비디오가 삭제 되었습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error deleting video: {}", e.getMessage(), e);
            
            // 오류 응답 메시지 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error deleting video");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
}


