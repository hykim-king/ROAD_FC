package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Road;
import com.pcwk.ehr.map.repository.RoadRepository;

@Service
public class RoadService {

	@Autowired
	RoadRepository roadRepository;
	
	public List<Road> findByAccident(){
		return roadRepository.findByAccident();
	}


	public List<Road> findByConstruction(){
		return roadRepository.findByConstruction();
	}
	
}