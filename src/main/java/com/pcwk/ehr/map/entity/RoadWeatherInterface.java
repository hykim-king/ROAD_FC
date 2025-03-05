package com.pcwk.ehr.map.entity;

public interface RoadWeatherInterface {
    Double getWeatherLat();
    Double getWeatherLon();
    String getFcstTime();
    String getFcstDate();
    String getCategory();
    String getFcstValue();
    String getRegionName();
}