package com.pcwk.ehr.map.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class RoadWeather {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer weather_id;
	
	@Column(nullable = false)
	private String baseDate;

	@Column(nullable = false)
	private String baseTime;
	
	@Column(nullable = false)
	private String fcstDate;
	
	@Column(nullable = false)
	private String fcstTime;
	
	@Column(nullable = false)
	private String fcstValue;
	
	@Column(nullable = false)
	private String category;
	
	@Column(columnDefinition = "NUMBER" , nullable = false)
	private Integer nx;
	
	@Column(columnDefinition = "NUMBER" , nullable = false)
	private Integer ny;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double weather_lat;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double weather_lon;
	
	@Column(nullable = false)
	private String regionName;
	
	@Column(columnDefinition = "DATE", nullable = false)
	private LocalDateTime weather_reg_dt;
	
}
