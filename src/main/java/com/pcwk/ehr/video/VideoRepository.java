package com.pcwk.ehr.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByVideoId(Long videoId);
    Page<Video> findAll(Specification<Video> spec, Pageable pageable);

    //Page<Video> findByTitleContaining(String keyword, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.videoId < :videoId ORDER BY v.videoId DESC LIMIT 4")
    List<Video> findTop4ByVideoIdLessThanOrderByVideoIdDesc(@Param("videoId") Long videoId);

    @Transactional
    @Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
    void increaseViewCount(@Param("id") Long id);
}




