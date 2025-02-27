package com.pcwk.ehr.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRAFFIC_DATA")
public class YearAccident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="TD_ID")
	private Long tdId;
	
	@Column(name="TD_YEAR",columnDefinition = "NUMBER")
	private Integer tdYear;
	
	@Column(name="TD_ACCIDENT",columnDefinition = "NUMBER")
	private Integer tdAccident;//연도별 토탈 사고건수
	
	@Column(name="TD_DEATH",columnDefinition = "NUMBER")
	private Integer tdDeathCnt;//연도별 토탈 사망건수
	
	@Column(name="TD_INJURY",columnDefinition = "NUMBER")
	private Integer tdInjuryCnt;//연도별 토탈 부상자수
	
	@Column(name="TD_REG_DT",columnDefinition = "DATE")
	private LocalDateTime tdRegDt;
	
	@OneToOne(mappedBy = "yearAccident")
	private YearAccidentRate yearAccidentRate;
}