package com.pcwk.ehr.video;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.pcwk.ehr.DataNotFoundException;

@ExtendWith(MockitoExtension.class)
public class VideoJunitTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video testVideo;
    private List<Video> videoList;

    @BeforeEach
    void setUp() {
        // 테스트용 비디오 객체 생성
        testVideo = new Video(1L, "테스트 비디오", "https://example.com/video1", 100, LocalDate.now(), "테스트 설명");
        
        // 테스트용 비디오 목록 생성
        Video video2 = new Video(2L, "두 번째 비디오", "https://example.com/video2", 200, LocalDate.now().minusDays(1), "두 번째 설명");
        Video video3 = new Video(3L, "세 번째 비디오 키워드", "https://example.com/video3", 300, LocalDate.now().minusDays(2), "세 번째 설명");
        videoList = Arrays.asList(testVideo, video2, video3);
    }

    @Test
    @DisplayName("비디오 조회 및 조회수 증가 테스트")
    void getVideoAndIncrementViewCountTest() {
        // given
        when(videoRepository.findByVideoId(1L)).thenReturn(Optional.of(testVideo));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Video video = videoService.getVideoAndIncrementViewCount(1L);

        // then
        assertNotNull(video);
        assertEquals(101, video.getViewCount()); // 조회수가 1 증가했는지 확인
        verify(videoRepository, times(1)).findByVideoId(1L);
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("페이징된 비디오 목록 조회 테스트")
    void getPagedVideosTest() {
        // given
        PageImpl<Video> videoPage = new PageImpl<>(videoList);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("regDate")));
        when(videoRepository.findAll(pageable)).thenReturn(videoPage);

        // when
        Page<Video> result = videoService.getPagedVideos(0, 10);

        // then
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(videoList, result.getContent());
        verify(videoRepository, times(1)).findAll(pageable);
    }

  
    


}