package com.pcwk.ehr.map.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.repository.TunnelRepository;

@Service
public class TunnelService {

	@Autowired
	TunnelRepository tunnelRepository;
	
	public List<Cctv> tunnelList(){
		
		return tunnelRepository.findTunnel();
	}
}
