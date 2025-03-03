package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.map.entity.Weather;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Integer> {
	@Query(value = "SELECT * FROM weather", nativeQuery = true)
	List<Weather> getWeatherInfo();
}
