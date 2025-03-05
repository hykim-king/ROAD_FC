package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Road;
import com.pcwk.ehr.map.repository.TestRoadRepository;

@Service
public class TestRoadService {

	@Autowired
	TestRoadRepository testRoadRepository;
	
	public List<Road> findByAccident(){
		return testRoadRepository.findByAccident();
	}


	public List<Road> findByConstruction(){
		return testRoadRepository.findByConstruction();
	}
	
}