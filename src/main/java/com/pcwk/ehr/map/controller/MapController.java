package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.freezing.Freezing;
import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.TestRoad;
import com.pcwk.ehr.map.service.CctvService;
import com.pcwk.ehr.map.service.FreezingService;
import com.pcwk.ehr.map.service.TestRoadService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/map")
public class MapController {
	@Autowired
	CctvService cctvService;
	
	@Autowired
	TestRoadService testRoadService;
	
	@Autowired
	FreezingService freezingService;
	
	@GetMapping("/map")
	public String cctvMap(Model model) {
		log.info("┌──────────────────┐");
		log.info("│ map()            │");
		log.info("└──────────────────┘");	
		
		List<TestRoad> accidentList = testRoadService.findByAccident();
		List<TestRoad> constructionList = testRoadService.findByConstruction();
		List<Cctv> cctvInfo = cctvService.list();
		List<Freezing> freezingInfo = freezingService.list();
		
		log.info("사고 데이터: {}", accidentList);
		log.info("공사 데이터: {}", constructionList);
		log.info("CCTV 데이터: {}", cctvInfo);
		log.info("결빙 데이터: {}", freezingInfo);
		
		
		model.addAttribute("accidentList", accidentList);
	    model.addAttribute("constructionList", constructionList);
	    model.addAttribute("cctvInfo", cctvInfo);
	    model.addAttribute("freezingInfo", freezingInfo);
	    
		return "map/map";
	}
}
