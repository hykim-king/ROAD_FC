package com.pcwk.ehr.news;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="news_id", nullable = false)
	private Integer id;
	
	@Column(name="news_title", length = 50, nullable = false)
	private String title;
	
	@Column(name="news_url", length = 255, nullable = false)
	private String url;
	
	@Column(name="news_newsroom", length = 30, nullable = false)
	private String newsroom;
	
	@Column(name="news_uploadedTime", length = 20, nullable = false)
	private String uploadedTime;
	
	@Column(name="news_reg_dt", columnDefinition = "DATE")
	private LocalDateTime regDt;
	

}
