package com.pcwk.ehr.map.Entity;

public interface TestWeatherInterface {
	
    Double getWeatherLat();
    Double getWeatherLon();
    String getFcstTime();
    String getFcstDate();
    String getCategory();
    String getFcstValue();
    String getRegionName();
}