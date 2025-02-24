package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.Entity.TestRoad;
import com.pcwk.ehr.map.Repository.TestRoadRepository;

@Service
public class TestRoadService {

	@Autowired
	TestRoadRepository testRoadRepository;
	
	public List<TestRoad> findByAccident(){
		return testRoadRepository.findByAccident();
	}


	public List<TestRoad> findByConstruction(){
		return testRoadRepository.findByConstruction();
	}
	
}