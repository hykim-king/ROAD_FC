package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.Entity.Road;
import com.pcwk.ehr.map.Entity.WeatherInterface;
import com.pcwk.ehr.map.Repository.RoadRepository;
import com.pcwk.ehr.map.Repository.WeatherRepository;

@Service
public class MapService {


	@Autowired
	RoadRepository testRoadRepository;
	
	@Autowired
	WeatherRepository testWeatherRepository;
	
	
	public List<Road> findByAccident(){
		return testRoadRepository.findByAccident();
	}


	public List<Road> findByConstruction(){
		return testRoadRepository.findByConstruction();
	}
	
	public List<WeatherInterface> findNowWeather(){
		return testWeatherRepository.findNowWeather();
	}
	
}
