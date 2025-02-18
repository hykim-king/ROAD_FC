package com.pcwk.ehr.freezing;

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
@Table(name = "FREEZING_INFO")
public class Freezing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FREEZING_ID", columnDefinition = "NUMBER")
	private Integer freezingId;
	
	@Column(name = "FREEZING_AGENCY", columnDefinition = "VARCHAR2(50 CHAR)")
	private String freezingAgency;
	
	@Column(name = "FREEZING_LENGTH", columnDefinition = "NUMBER")
	private Integer freezingLength;
	
	@Column(name = "FREEZING_START_LAT", columnDefinition = "NUMBER")
	private Integer freezingStartLat;
	
	@Column(name = "FREEZING_START_LON", columnDefinition = "NUMBER")
	private Integer freezingStartLon;
	
	@Column(name = "FREEZING_END_LAT", columnDefinition = "NUMBER")
	private Integer freezingEndLat;
	
	@Column(name = "FREEZING_END_LON", columnDefinition = "NUMBER")
	private Integer freezingEndLon;
	
	@Column(name = "FREEZING_REG_DT", columnDefinition = "DATE")
	private LocalDateTime freezingRegDt;
}