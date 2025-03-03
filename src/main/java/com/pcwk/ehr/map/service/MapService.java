package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.freezing.Freezing;
import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.TestRoad;
import com.pcwk.ehr.map.entity.TestWeatherInterface;
import com.pcwk.ehr.map.repository.CctvRepository;
import com.pcwk.ehr.map.repository.FreezingRepository;
import com.pcwk.ehr.map.repository.TestRoadRepository;
import com.pcwk.ehr.map.repository.TestWeatherRepository;
import com.pcwk.ehr.map.repository.TunnelRepository;

@Service
public class MapService {
	@Autowired
	TestRoadRepository testRoadRepository;
	
	@Autowired
	TestWeatherRepository testWeatherRepository;
	
	@Autowired
	CctvRepository cctvRepository;
	
	@Autowired
	FreezingRepository freezingRepository;
	
	@Autowired
	TunnelRepository tunnelRepository;
	
	public List<TestRoad> findByAccident(){
		return testRoadRepository.findByAccident();
	}

	public List<TestRoad> findByConstruction(){
		return testRoadRepository.findByConstruction();
	}
	
	public List<TestWeatherInterface> findNowWeather(){
		return testWeatherRepository.findNowWeather();
	}
	
	public List<Freezing> freezingList(){
		return freezingRepository.findAll();
	}
	
	public List<Cctv> cctvList(){
		return cctvRepository.findAll();
	}
	
	public List<Cctv> tunnelList(){
		return tunnelRepository.findTunnel();
	}
}
