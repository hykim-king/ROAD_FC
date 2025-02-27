package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.Freezing;
import com.pcwk.ehr.map.service.CctvService;
import com.pcwk.ehr.map.service.FreezingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/cctv")
public class CctvController {

	@Autowired
	CctvService cctvService;
	
	@Autowired
	FreezingService freezingService;
	
	@GetMapping("/map")
	public String map(Model model) {
		log.info("┌──────────────────┐");
		log.info("│ map()            │");
		log.info("└──────────────────┘");	
		List<Cctv> cctvInfo = cctvService.list();
		List<Freezing> freezingInfo = freezingService.list();
		
		log.info("CCTV 데이터: {}", cctvInfo);	
		model.addAttribute("cctvInfo", cctvInfo);
		model.addAttribute("freezingInfo", freezingInfo);
		
		return "map/map";
	}	
}