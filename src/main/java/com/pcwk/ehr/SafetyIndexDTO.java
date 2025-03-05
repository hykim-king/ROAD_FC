package com.pcwk.ehr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SafetyIndexDTO {
	private String region;
	//private double stationLat;
	//private double stationLon;
	private int safetyScore;
}
