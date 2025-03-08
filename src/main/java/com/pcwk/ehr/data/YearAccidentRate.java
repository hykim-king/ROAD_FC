package com.pcwk.ehr.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRAFFIC_RATE")
public class YearAccidentRate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="RATE_ID")
	private Long rateId;
	
	@Column(name = "TA_YEAR",columnDefinition = "NUMBER",insertable = false,updatable = false)
	private Integer taYear;
	
	@Column(name = "TA_DEATH_RATE",columnDefinition = "NUMBER")
	private Integer taDeathRate;
	
	@Column(name = "TA_INJURY_RATE",columnDefinition = "NUMBER")
	private Integer taInjuryRate;
	
	@Column(name = "TA_FATAL_RATE",columnDefinition = "NUMBER")
	private Integer taFatalRate;
	
	@Column(name = "TA_ACCIDENT_RATE",columnDefinition = "NUMBER")
	private Integer taAccidentRate;
	
	@Column(name="TA_RATE_REG_DT", columnDefinition = "DATE")
	private LocalDateTime taRateRegDt;
	  
	@OneToOne
	@JoinColumn(name = "TA_YEAR", referencedColumnName = "TD_YEAR")
	private YearAccident yearAccident;
}