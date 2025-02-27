package com.pcwk.ehr.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRAFFIC_ACCIDENT")
public class TrafficAccident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="TA_ID")
	private Long taId;
	
	@Column(name = "TA_ACCIDENT_YEAR",columnDefinition = "NUMBER")
	private Integer taAccidentYear;
	
	@Column(name = "TA_ACCIDENT_DATE")
	private String taAccidentDate;
	
	@Column(name = "TA_TIMEZONE")
	private String taTimeZone;
	
	@Column(name = "TA_ACCIDENT_TOTAL",columnDefinition = "NUMBER")
	private Integer taAccidentCnt;
	
	@Column(name = "TA_DEATH_TOTAL",columnDefinition = "NUMBER")
	private Integer taDeathTotalCnt;
	
	@Column(name = "TA_INJURY_TOTAL",columnDefinition = "NUMBER")
	private Integer taInjuryTotalCnt;
	
	@Column(name="TA_REG_DT", columnDefinition = "DATE")
	private LocalDateTime taRegDt;
	
}