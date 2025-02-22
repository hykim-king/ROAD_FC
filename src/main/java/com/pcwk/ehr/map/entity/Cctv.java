package com.pcwk.ehr.map.entity;


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
public class Cctv {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer cctv_id;
	
	@Column(length = 50 , nullable = false)
	private String cctv_name;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double cctv_lon;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private double cctv_lat;
	
	@Column(columnDefinition = "DATE", nullable = false)
	private LocalDateTime cctv_reg_dt;
	
}