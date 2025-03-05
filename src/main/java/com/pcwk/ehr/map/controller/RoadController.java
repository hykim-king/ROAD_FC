package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pcwk.ehr.map.entity.Road;
import com.pcwk.ehr.map.service.RoadService;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
@RequestMapping("/road")
public class RoadController {
	
	@Autowired
	RoadService roadService;
	
	@GetMapping("/map")
	public String roadMap(Model model) throws JsonProcessingException {
		log.info("┌──────────────────┐");
		log.info("│ roadMap()        │");
		log.info("└──────────────────┘");	
		
		List<Road> accidentList = roadService.findByAccident();
		List<Road> constructionList = roadService.findByConstruction();
		
		log.info("사고 데이터: {}", accidentList);
		log.info("공사 데이터: {}", constructionList);
		
//		ObjectMapper objectMapper = new ObjectMapper();
//	    String accidentListJson = objectMapper.writeValueAsString(accidentList);
//	    String constructionListJson = objectMapper.writeValueAsString(constructionList);

	    model.addAttribute("accidentList", accidentList);
	    model.addAttribute("constructionList", constructionList);
		
		return "map/road";
		
	}

}