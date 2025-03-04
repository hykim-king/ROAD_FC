package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.map.Entity.Road;
import com.pcwk.ehr.map.Entity.WeatherInterface;
import com.pcwk.ehr.map.service.MapService;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
@RequestMapping("/road")
public class RoadController {
	
	@Autowired
	MapService mapService;
	
	@GetMapping("/map")
	public String roadMap(Model model) {
		log.info("┌──────────────────┐");
		log.info("│ roadMap()        │");
		log.info("└──────────────────┘");	
		
		List<Road> accidentList = mapService.findByAccident();
		List<Road> constructionList = mapService.findByConstruction();
		List<WeatherInterface> findNowWeather = mapService.findNowWeather();
		
//		log.info("사고 데이터: {}", accidentList);
//		log.info("공사 데이터: {}", constructionList);
		
//		for (TestWeatherInterface wp : findNowWeather) {
//		    log.info("Lat: " + wp.getWeatherLat() + ", Lon: " + wp.getWeatherLon() + 
//		    		", FcstTime: " + wp.getFcstTime() + ", FcstDate: " + wp.getFcstDate() + 
//		    		", Category: " + wp.getCategory() + ", FcstValue: " + wp.getFcstValue());
//		}
		
		model.addAttribute("accidentList", accidentList);
		model.addAttribute("constructionList", constructionList);
		model.addAttribute("findNowWeather", findNowWeather);
		
		return "map/road";
		
	}

}
