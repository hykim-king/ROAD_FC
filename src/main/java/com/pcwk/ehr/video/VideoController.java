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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


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
    public String detail(Model model, @PathVariable("videoId") Long videoId) {
    	log.info("┌──────────────────┐");
		log.info("│detail()          │");
		log.info("└──────────────────┘");
        log.info("Fetching video details - Video ID: {}", videoId);

        try {
            Video video = videoService.getVideoAndIncrementViewCount(videoId);
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
        if (url.contains("youtu.be/")) {
            return "https://www.youtube.com/embed/" + url.split("youtu.be/")[1].split("\\?")[0];
        } else if (url.contains("watch?v=")) {
            return "https://www.youtube.com/embed/" + url.split("v=")[1].split("&")[0];
        }
        return url;
    }
   
    /**
    @PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal princial) {
		log.info("┌──────────────────┐");
		log.info("│ questionModify() │");
		log.info("└──────────────────┘");

		log.info("id:{}", id);

		Video video  = videoService.getVideoAndIncrementViewCount(id);
		log.info("question:{}", question);

		log.info("question.getAuthor().getUsername():{}", question.getAuthor().getUsername());
		log.info("princial.getName():{}", princial.getName());
		if (!question.getAuthor().getUsername().equals(princial.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		//
		questionForm.setSubject(video.getSubject());
		questionForm.setContent(video.getContent());

		return "question/question_form";
	}
**/
}
