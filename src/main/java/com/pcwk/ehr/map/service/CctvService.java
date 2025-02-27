package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.repository.CctvRepository;

@Service
public class CctvService {
	
	@Autowired
	CctvRepository cctvRepository;
	
	
	public List<Cctv> list(){
		
		return cctvRepository.findAll();
	}
	
}
