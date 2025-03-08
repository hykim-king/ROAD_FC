package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.freezing.Freezing;
import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.Road;
import com.pcwk.ehr.map.entity.RoadWeatherInterface;
import com.pcwk.ehr.map.repository.CctvRepository;
import com.pcwk.ehr.map.repository.FreezingRepository;
import com.pcwk.ehr.map.repository.RoadRepository;
import com.pcwk.ehr.map.repository.RoadWeatherRepository;
import com.pcwk.ehr.map.repository.TunnelRepository;

@Service
public class MapService {
	@Autowired
	RoadRepository roadRepository;
	
	@Autowired
	RoadWeatherRepository roadWeatherRepository;
	
	@Autowired
	CctvRepository cctvRepository;
	
	@Autowired
	FreezingRepository freezingRepository;
	
	@Autowired
	TunnelRepository tunnelRepository;
	
	public List<Road> findByAccident(){
		return roadRepository.findByAccident();
	}

	public List<Road> findByConstruction(){
		return roadRepository.findByConstruction();
	}
	
	public List<RoadWeatherInterface> findNowWeather(){
		return roadWeatherRepository.findNowWeather();
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
