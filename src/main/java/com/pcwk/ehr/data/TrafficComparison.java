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
@Table(name="TRAFFIC_COMPARISON")
public class TrafficComparison {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long tcId;
	
	@Column(name = "TC_STDYEAR", columnDefinition = "NUMBER")
	private Integer tcStdYear;
	
	@Column(name = "TC_SYDHOUR", columnDefinition = "NUMBER")
	private Integer tcSydHour;

	@Column(name = "TC_SPHLDFTTNM")
	private String tcSphldfttNm;
	
	@Column(name = "TC_SPHLDFTTSCOPTYPENM")
	private String tcSphldfttScopTypeNm;
	
	@Column(name = "TC_TRFL", columnDefinition = "NUMBER")
	private Integer tcTrfl;
	
	@Column(name = "TC_PREV_TRFL", columnDefinition = "NUMBER")
	private Integer tcPrevTrfl;
	
	@Column(name = "TC_CHANGE_TRFL", columnDefinition = "NUMBER")
	private Integer tcChangeTrfl;
	
	@Column(name = "TC_RATE_TRFL", columnDefinition = "NUMBER")
	private Integer tcRateTrfl;
	
	@Column(name = "TC_REG_DT",columnDefinition = "DATE")
	private LocalDateTime tcRegDt;
}
