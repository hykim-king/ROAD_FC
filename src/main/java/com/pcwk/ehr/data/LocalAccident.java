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
@Table(name = "LOCAL_ACCIDENT")
public class LocalAccident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="LA_ID")
	private Long laId;
	
	@Column(name = "LA_YEAR",columnDefinition = "NUMBER")
	private Integer laYear;
	
	@Column(name = "LA_MAJOR_REGION")
	private String laMajorRegion;
	
	@Column(name = "LA_MINOR_REGION")
	private String laMinorRegion;
	
	@Column(name = "LA_LOCAL_CNT",columnDefinition = "NUMBER")
	private Integer laLocalCnt;
	
	@Column(name = "LA_DEATH_CNT",columnDefinition = "NUMBER")
	private Integer laDeathCnt;
	
	@Column(name = "LA_INJURY_CNT",columnDefinition = "NUMBER")
	private Integer laInjuryCnt;
	
	@Column(name = "LA_SERIOUS_CNT",columnDefinition = "NUMBER")
	private Integer laSeriousCnt;
	
	@Column(name = "LA_MINOR_CNT",columnDefinition = "NUMBER")
	private Integer laMinorCnt;
	
	@Column(name = "LA_SAFE_SCORE",columnDefinition = "NUMBER")
	private Integer laSafeScore;
	
	@Column(name = "LA_GRADE",columnDefinition = "NUMBER")
	private Integer laGrade;
	
	@Column(name="LA_REG_DT",columnDefinition = "DATE")
	private LocalDateTime laRegDt;
	
}