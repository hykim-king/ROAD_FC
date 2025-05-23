package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.map.entity.RoadWeather;
import com.pcwk.ehr.map.entity.RoadWeatherInterface;

@Repository
public interface RoadWeatherRepository extends JpaRepository<RoadWeather, Integer>{
    @Query("SELECT r.category AS category, " +
            "       FUNCTION('TO_CHAR', FUNCTION('TO_DATE', r.fcstDate, 'YYYY-MM-DD'), 'YYYY-MM-DD') AS fcstDate, " +
            "       FUNCTION('TO_CHAR', FUNCTION('TO_DATE', r.fcstTime, 'HH24:MI'), 'HH24:MI') AS fcstTime, " +
            "       r.fcstValue AS fcstValue, " +
            "       r.weather_lat AS weatherLat, " +
            "       r.weather_lon AS weatherLon, " +
            "		r.regionName AS regionName	"+	
            "FROM RoadWeather r " +
            "WHERE FUNCTION('TO_CHAR', FUNCTION('TO_DATE', r.fcstDate, 'YYYY-MM-DD'), 'YYYY-MM-DD') = FUNCTION('TO_CHAR', CURRENT_DATE, 'YYYY-MM-DD') " +
            "AND r.fcstTime = FUNCTION('TO_CHAR', CURRENT_TIME, 'HH24') || '00'")
	List<RoadWeatherInterface> findNowWeather();
}