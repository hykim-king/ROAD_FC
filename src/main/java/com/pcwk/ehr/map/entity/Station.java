package com.pcwk.ehr.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Station {
	
	@Id
	private Integer station_id;
	
	@Column(name="station_area")
	private String station_area;
	
	@Column(name="station_latitudE", columnDefinition = "NUMBER")
	private double station_latitude;
	
	@Column(name="station_longitude", columnDefinition = "NUMBER")
	private double station_longitude;
	
}
