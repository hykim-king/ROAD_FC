package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.Entity.Cctv;
import com.pcwk.ehr.map.Entity.TestRoad;
import com.pcwk.ehr.map.Entity.TestWeatherInterface;
import com.pcwk.ehr.map.Repository.CctvRepository;
import com.pcwk.ehr.map.Repository.TestRoadRepository;
import com.pcwk.ehr.map.Repository.TestWeatherRepository;

@Service
public class MapService {

	@Autowired
	CctvRepository cctvRepository;
	
	@Autowired
	TestRoadRepository testRoadRepository;
	
	@Autowired
	TestWeatherRepository testWeatherRepository;
	
	
	public List<Cctv> list(){
		return cctvRepository.findAll();
	}
	
	
	public List<TestRoad> findByAccident(){
		return testRoadRepository.findByAccident();
	}


	public List<TestRoad> findByConstruction(){
		return testRoadRepository.findByConstruction();
	}
	
	public List<TestWeatherInterface> findNowWeather(){
		return testWeatherRepository.findNowWeather();
	}
	
}
