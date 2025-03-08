package com.pcwk.ehr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SafetyIndexDTO {
	private int stationId;
	private String region;
	private Double stationLat;
	private Double stationLon;
	private int safetyScore;
	
    public SafetyIndexDTO(String region, int safetyScore) {
    	this.stationId = 0;
        this.region = region;
        this.stationLat = null;
        this.stationLon = null;
        this.safetyScore = safetyScore;
    }
}
