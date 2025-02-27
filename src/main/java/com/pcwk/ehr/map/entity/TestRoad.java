package com.pcwk.ehr.map.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class TestRoad {
	
	@Id
	@Column
	private String road_id;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private Integer road_type;
	
	@Column(length = 200, nullable = false)
	private String road_addr;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double road_lon;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double road_lat;
	
	@Column(length = 500, nullable = false)
	private String road_title;
	
	@Column(length = 50, nullable = false)
	private String road_start;
	
	@Column(length = 50, nullable = false)
	private String road_end;
	
	@Column(length = 50)
	private String road_update;
	
	@Column(columnDefinition = "DATE", nullable = false)
	private LocalDateTime road_reg_dt;
}