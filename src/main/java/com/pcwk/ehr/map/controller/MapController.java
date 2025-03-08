package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.map.entity.Freezing;
import com.pcwk.ehr.map.service.FreezingService;
import com.pcwk.ehr.map.service.WeatherService;
import com.pcwk.ehr.station.Station;

@RequestMapping("/map")
@Controller
public class MapController {
	
	@Autowired
	FreezingService freezingService;
	
	@Autowired
	WeatherService weatherService;

	@GetMapping("mainMap")
	public String mainMap(Model model) {
		
		List<Freezing> freezingInfo = freezingService.freezingList();
		 
		model.addAttribute("freezingInfo", freezingInfo);
		List<Station> staionList = weatherService.stationList();
		model.addAttribute("stationList", staionList);
		
		return "map/mainMap";
	}
	
	@GetMapping("polygonMap")
	public String polygonMap() {
		
		return "map/polygonMap";
	}

}
