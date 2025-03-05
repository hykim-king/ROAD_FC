package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pcwk.ehr.map.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {

	List<Weather> findAll();

	@Query(value = "SELECT w.WEATHER_STATION_ID, w.WEATHER_PRECIPITATION, w.WEATHER_SNOWFALL, w.WEATHER_VISIBILITY_DISTANCE, w.WEATHER_WIND_SPEED, w.WEATHER_TEMPERATURE, s.station_latitude, s.station_longitude "
			+ "FROM WEATHER w "
			+ "INNER JOIN STATION s "
			+ "ON w.weather_station_id = s.station_id", nativeQuery=true )
	List<Object[]> dataNeededForSafetyIndex();
}