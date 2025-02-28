package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pcwk.ehr.map.entity.Freezing;
import com.pcwk.ehr.map.repository.FreezingRepository;

@Service
public class FreezingService {

	@Autowired
	FreezingRepository freezingRepository;
	
	public List<Freezing> freezingList(){
		return freezingRepository.findAll();
	}
}
